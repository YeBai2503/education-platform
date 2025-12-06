package com.baymax.exam.homework.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.center.enums.QuestionResultTypeEnum;
import com.baymax.exam.center.enums.QuestionTypeEnum;
import com.baymax.exam.center.feign.QuestionClient;
import com.baymax.exam.center.model.Question;
import com.baymax.exam.center.vo.QuestionInfoVo;
import com.baymax.exam.common.core.result.PageResult;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.homework.enums.HomeworkAnswerLogEnum;
import com.baymax.exam.homework.model.HomeworkAnswerLog;
import com.baymax.exam.homework.model.HomeworkAnswerResult;
import com.baymax.exam.homework.model.HomeworkInfo;
import com.baymax.exam.homework.model.HomeworkScoreRecord;
import com.baymax.exam.homework.service.IHomeworkQuestionService;
import com.baymax.exam.homework.service.impl.HomeworkAnswerLogServiceImpl;
import com.baymax.exam.homework.service.impl.HomeworkAnswerResultServiceImpl;
import com.baymax.exam.homework.service.impl.HomeworkInfoServiceImpl;
import com.baymax.exam.homework.service.impl.HomeworkScoreRecordServiceImpl;
import com.baymax.exam.homework.vo.HomeworkAnswerQuestionResultVo;
import com.baymax.exam.homework.vo.StudentHomeworkReviewVo;
import com.baymax.exam.user.feign.ClassesClient;
import com.baymax.exam.user.feign.UserClient;
import com.baymax.exam.user.model.Classes;
import com.baymax.exam.user.model.User;
import com.baymax.exam.user.model.UserAuthInfo;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业批阅控制器
 * </p>
 *
 */
@Slf4j
@Tag(name = "作业批阅")
@RestController
@RequestMapping("/homework-review")
public class HomeworkReviewController {
    
    @Autowired
    private HomeworkInfoServiceImpl homeworkInfoService;
    
    @Autowired
    private HomeworkAnswerLogServiceImpl homeworkAnswerLogService;
    
    @Autowired
    private HomeworkAnswerResultServiceImpl homeworkAnswerResultService;
    
    @Autowired
    private HomeworkScoreRecordServiceImpl homeworkScoreRecordService;
    
    @Autowired
    private IHomeworkQuestionService homeworkQuestionService;
    
    @Autowired
    private QuestionClient questionClient;
    
    @Autowired
    private ClassesClient classesClient;
    
    @Autowired
    private UserClient userClient;
    
    @Operation(summary = "获取待批阅学生列表")
    @GetMapping("/student-list/{homeworkInfoId}")
    public Result getStudentList(
            @PathVariable Integer homeworkInfoId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer classId) {
        
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        if (homeworkInfo == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        Integer userId = UserAuthUtil.getUserId();
        if (!Objects.equals(homeworkInfo.getTeacherId(), userId)) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该作业的教师");
        }
        
        // 获取提交作业的学生日志
        List<HomeworkAnswerLog> submitLogs = homeworkAnswerLogService.getLogListByAction(homeworkInfoId, HomeworkAnswerLogEnum.SUBMIT);
        if (submitLogs.isEmpty()) {
            return Result.success(new PageResult<>());
        }
        
        // 获取学生ID列表
        List<Integer> studentIds = submitLogs.stream()
            .map(HomeworkAnswerLog::getStudentId)
            .distinct()
            .collect(Collectors.toList());
        
        // 如果指定了班级ID，过滤该班级的学生
        if (classId != null) {
            List<Integer> classStudentIds = submitLogs.stream()
                .filter(log -> Objects.equals(log.getClassId(), classId))
                .map(HomeworkAnswerLog::getStudentId)
                .distinct()
                .collect(Collectors.toList());
            studentIds.retainAll(classStudentIds);
        }
        
        // 分页处理
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, studentIds.size());
        
        if (start >= studentIds.size()) {
            return Result.success(new PageResult<>());
        }
        
        List<Integer> pageStudentIds = studentIds.subList(start, end);
        
        // 获取学生信息
        List<UserAuthInfo> studentInfos = new ArrayList<>();
        for (Integer studentId : pageStudentIds) {
            User user = userClient.getBaseUserInfoById(studentId);
            if (user != null) {
                UserAuthInfo userAuthInfo = new UserAuthInfo();
                userAuthInfo.setUserId(user.getId());
                userAuthInfo.setUsername(user.getUsername());
                userAuthInfo.setNickname(user.getNickname());
                userAuthInfo.setPicture(user.getPicture());
                studentInfos.add(userAuthInfo);
            }
        }
        
        // 构造分页结果
        PageResult<UserAuthInfo> pageResult = new PageResult<>();
        pageResult.setList(studentInfos);
        pageResult.setTotal(studentIds.size());
        pageResult.setPages((int) Math.ceil((double) studentIds.size() / pageSize));
        pageResult.setCurrent(page);
        
        return Result.success(pageResult);
    }
    
    @Operation(summary = "获取学生作业详情")
    @GetMapping("/student-detail/{homeworkInfoId}/{studentId}")
    public Result getStudentDetail(
            @PathVariable Integer homeworkInfoId,
            @PathVariable Integer studentId) {
        
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        if (homeworkInfo == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        Integer userId = UserAuthUtil.getUserId();
        if (!Objects.equals(homeworkInfo.getTeacherId(), userId)) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该作业的教师");
        }
        
        // 获取学生信息
        User user = userClient.getBaseUserInfoById(studentId);
        if (user == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "学生不存在");
        }
        UserAuthInfo studentInfo = new UserAuthInfo();
        studentInfo.setUserId(user.getId());
        studentInfo.setUsername(user.getUsername());
        studentInfo.setNickname(user.getNickname());
        studentInfo.setPicture(user.getPicture());
        
        // 获取作业题目
        List<Question> questions = homeworkQuestionService.getQuestionByHomeworkId(homeworkInfo.getHomeworkId());
        if (questions.isEmpty()) {
            return Result.failed(ResultCode.PARAM_ERROR, "作业题目不存在");
        }
        
        // 获取学生作答结果
        List<HomeworkAnswerResult> answerResultList = homeworkAnswerResultService.getAnswerResultListByUserId(homeworkInfoId, studentId);
        
        // 获取学生得分记录
        List<HomeworkScoreRecord> scoreRecords = homeworkScoreRecordService.getScoreListByUserId(homeworkInfoId, studentId);
        
        // 获取学生提交日志
        HomeworkAnswerLog submitLog = homeworkAnswerLogService.getStudentLogOne(homeworkInfoId, studentId, HomeworkAnswerLogEnum.SUBMIT);
        
        // 获取学生开始作业日志
        HomeworkAnswerLog startLog = homeworkAnswerLogService.getStudentLogOne(homeworkInfoId, studentId, HomeworkAnswerLogEnum.START);
        
        // 获取批阅状态
        HomeworkAnswerLog reviewLog = homeworkAnswerLogService.getStudentLogOne(homeworkInfoId, studentId, HomeworkAnswerLogEnum.TEACHER_REVIEW);
        if (reviewLog == null) {
            reviewLog = homeworkAnswerLogService.getStudentLogOne(homeworkInfoId, studentId, HomeworkAnswerLogEnum.ROBOT_REVIEW);
        }
        
        // 构造学生作业评阅视图
        StudentHomeworkReviewVo reviewVo = new StudentHomeworkReviewVo();
        reviewVo.setUserAuthInfo(studentInfo);
        reviewVo.setSubmitTime(submitLog != null ? submitLog.getCreatedAt() : null);
        reviewVo.setStartTime(startLog != null ? startLog.getCreatedAt() : null);
        reviewVo.setAnswerStatus(reviewLog != null ? reviewLog.getStatus() : HomeworkAnswerLogEnum.START);
        
        // 计算总分
        float totalScore = 0f;
        for (HomeworkScoreRecord record : scoreRecords) {
            totalScore += record.getScore();
        }
        reviewVo.setScore(totalScore);
        
        // 统计正确题目数量
        int correctCount = 0;
        for (HomeworkScoreRecord record : scoreRecords) {
            if (record.getResultType() == QuestionResultTypeEnum.CORRECT) {
                correctCount++;
            }
        }
        reviewVo.setCorrectNumber(correctCount);
        
        // 统计已批阅题目数量
        reviewVo.setReviewCount(scoreRecords.size());
        reviewVo.setReviewTotal(questions.size());
        
        // 获取班级信息
        if (submitLog != null && submitLog.getClassId() != null) {
            Classes classInfo = classesClient.getClassByUserId(homeworkInfo.getCourseId(), studentId);
            if (classInfo != null) {
                reviewVo.setCourseClassName(classInfo.getName());
            }
        }
        
        // 构造题目作答结果
        Map<QuestionTypeEnum, List<HomeworkAnswerQuestionResultVo>> questionResults = new HashMap<>();
        for (Question question : questions) {
            HomeworkAnswerQuestionResultVo resultVo = new HomeworkAnswerQuestionResultVo();
            
            // 设置题目信息
            QuestionInfoVo questionInfoVo = new QuestionInfoVo();
            questionInfoVo.setId(question.getId());
            questionInfoVo.setContent(question.getContent());
            questionInfoVo.setType(question.getType());
            questionInfoVo.setScore(question.getScore());
            resultVo.setQuestionInfo(questionInfoVo);
            
            // 设置作答结果
            List<HomeworkAnswerResult> questionAnswers = answerResultList.stream()
                .filter(result -> Objects.equals(result.getQuestionId(), question.getId()))
                .collect(Collectors.toList());
            resultVo.setAnswerResult(questionAnswers);
            
            // 设置批阅结果
            Optional<HomeworkScoreRecord> scoreRecord = scoreRecords.stream()
                .filter(record -> Objects.equals(record.getQuestionId(), question.getId()))
                .findFirst();
            scoreRecord.ifPresent(resultVo::setScoreRecord);
            
            // 按题目类型分组
            questionResults.computeIfAbsent(question.getType(), k -> new ArrayList<>()).add(resultVo);
        }
        reviewVo.setAnswerResults(questionResults);
        
        return Result.success(reviewVo);
    }
    
    @Operation(summary = "批阅作业")
    @PostMapping("/review/{homeworkInfoId}/{studentId}")
    public Result reviewHomework(
            @PathVariable Integer homeworkInfoId,
            @PathVariable Integer studentId,
            @RequestBody @Validated List<HomeworkScoreRecord> scoreRecords) {
        
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        if (homeworkInfo == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        Integer userId = UserAuthUtil.getUserId();
        if (!Objects.equals(homeworkInfo.getTeacherId(), userId)) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该作业的教师");
        }
        
        // 设置作业信息ID和学生ID
        for (HomeworkScoreRecord record : scoreRecords) {
            record.setHomeworkInfoId(homeworkInfoId);
            record.setUserId(studentId);
        }
        
        // 保存或更新得分记录
        homeworkScoreRecordService.saveOrUpdateBatch(scoreRecords);
        
        // 更新批阅状态
        homeworkAnswerLogService.saveReviewStatus(homeworkInfoId, studentId, HomeworkAnswerLogEnum.TEACHER_REVIEW);
        
        return Result.msgSuccess("批阅成功");
    }
} 