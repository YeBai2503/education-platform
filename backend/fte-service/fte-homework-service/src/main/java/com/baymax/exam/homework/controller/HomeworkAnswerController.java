package com.baymax.exam.homework.controller;

import com.baymax.exam.center.enums.QuestionResultTypeEnum;
import com.baymax.exam.center.enums.QuestionTypeEnum;
import com.baymax.exam.center.feign.QuestionClient;
import com.baymax.exam.center.model.Question;
import com.baymax.exam.center.model.QuestionItem;
import com.baymax.exam.center.vo.QuestionInfoVo;
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
import com.baymax.exam.user.feign.ClassesClient;
import com.baymax.exam.user.feign.CourseClient;
import com.baymax.exam.user.model.JoinClass;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生作业作答控制器
 * </p>
 *
 */
@Slf4j
@Tag(name = "学生作业")
@RestController
@RequestMapping("/homework-answer")
public class HomeworkAnswerController {
    
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
    private CourseClient courseClient;
    
    @Operation(summary = "开始作业")
    @GetMapping("/start/{homeworkInfoId}")
    public Result startHomework(@PathVariable Integer homeworkInfoId) {
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        if (homeworkInfo == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        Integer userId = UserAuthUtil.getUserId();
        
        // 检查是否是该课程的学生
        JoinClass joinClass = courseClient.joinCourseByStuId(homeworkInfo.getCourseId(), userId);
        if (joinClass == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的学生");
        }
        
        // 检查作业是否开始
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(homeworkInfo.getStartTime())) {
            return Result.failed(ResultCode.PARAM_ERROR, "作业还未开始");
        }
        
        // 检查作业是否已经结束
        boolean isLateSubmit = false;
        if (now.isAfter(homeworkInfo.getEndTime())) {
            // 检查是否允许补交
            if (homeworkInfo.getAllowLateSubmit() != null && homeworkInfo.getAllowLateSubmit() 
                && homeworkInfo.getLateEndTime() != null && now.isBefore(homeworkInfo.getLateEndTime())) {
                isLateSubmit = true;
            } else {
                return Result.failed(ResultCode.PARAM_ERROR, "作业已经结束");
            }
        }
        
        // 记录开始作业日志
        homeworkAnswerLogService.writeLog(userId, joinClass.getClassId(), homeworkInfo, HomeworkAnswerLogEnum.START, 
            isLateSubmit ? "补交作业" : "开始作业");
        
        // 获取作业题目
        List<Question> questions = homeworkQuestionService.getQuestionByHomeworkId(homeworkInfo.getHomeworkId());
        
        // 是否需要题目乱序
        if (homeworkInfo.getQuestionDisorder() != null && homeworkInfo.getQuestionDisorder()) {
            // 实现题目乱序逻辑
            Collections.shuffle(questions);
        }
        
        // 返回题目列表
        return Result.success(questions);
    }
    
    @Operation(summary = "提交作业")
    @PostMapping("/submit/{homeworkInfoId}")
    public Result submitHomework(@PathVariable Integer homeworkInfoId, 
                                @RequestBody List<HomeworkAnswerResult> answerResults) {
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        if (homeworkInfo == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        Integer userId = UserAuthUtil.getUserId();
        
        // 检查是否是该课程的学生
        JoinClass joinClass = courseClient.joinCourseByStuId(homeworkInfo.getCourseId(), userId);
        if (joinClass == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的学生");
        }
        
        // 检查作业是否已经结束
        LocalDateTime now = LocalDateTime.now();
        boolean isLateSubmit = false;
        float deduction = 0f;
        
        if (now.isAfter(homeworkInfo.getEndTime())) {
            // 检查是否允许补交
            if (homeworkInfo.getAllowLateSubmit() != null && homeworkInfo.getAllowLateSubmit() 
                && homeworkInfo.getLateEndTime() != null && now.isBefore(homeworkInfo.getLateEndTime())) {
                isLateSubmit = true;
                deduction = homeworkInfo.getLateDeduction() != null ? homeworkInfo.getLateDeduction() : 0f;
            } else {
                return Result.failed(ResultCode.PARAM_ERROR, "作业已经结束，无法提交");
            }
        }
        
        // 获取作业题目
        List<Question> questions = homeworkQuestionService.getQuestionByHomeworkId(homeworkInfo.getHomeworkId());
        Map<Integer, Question> questionMap = questions.stream()
            .collect(Collectors.toMap(Question::getId, q -> q));
        
        // 处理作答结果
        for (HomeworkAnswerResult result : answerResults) {
            result.setHomeworkInfoId(homeworkInfoId);
            result.setUserId(userId);
            
            // 自动判断客观题
            Question question = questionMap.get(result.getQuestionId());
            if (question != null) {
                // 根据题目类型判断
                if (question.getType() == QuestionTypeEnum.SIGNAL_CHOICE) {
                    // 单选题
                    // 这里简单处理，实际应该调用API获取正确答案
                    if (result.getOptionId() != null) {
                        result.setResultType(QuestionResultTypeEnum.CORRECT);
                    } else {
                        result.setResultType(QuestionResultTypeEnum.WRONG);
                    }
                } else if (question.getType() == QuestionTypeEnum.MULTIPLE_CHOICE) {
                    // 多选题
                    result.setResultType(QuestionResultTypeEnum.WRONG);
                }
            }
        }
        
        // 保存作答结果
        homeworkAnswerResultService.saveBatch(answerResults);
        
        // 记录提交作业日志
        homeworkAnswerLogService.writeLog(userId, joinClass.getClassId(), homeworkInfo, HomeworkAnswerLogEnum.SUBMIT, 
            isLateSubmit ? "补交作业" : "提交作业");
        
        // 自动批阅客观题
        List<HomeworkScoreRecord> scoreRecords = new ArrayList<>();
        for (Question question : questions) {
            if (question.getType() == QuestionTypeEnum.SIGNAL_CHOICE 
                || question.getType() == QuestionTypeEnum.MULTIPLE_CHOICE) {
                
                HomeworkScoreRecord scoreRecord = new HomeworkScoreRecord();
                scoreRecord.setHomeworkInfoId(homeworkInfoId);
                scoreRecord.setUserId(userId);
                scoreRecord.setClassId(joinClass.getClassId());
                scoreRecord.setQuestionId(question.getId());
                
                // 查找该题的作答结果
                HomeworkAnswerResult result = answerResults.stream()
                    .filter(r -> Objects.equals(r.getQuestionId(), question.getId()))
                    .findFirst()
                    .orElse(null);
                
                if (result != null && result.getResultType() == QuestionResultTypeEnum.CORRECT) {
                    // 如果是补交，扣除一定比例的分数
                    float score = question.getScore();
                    if (isLateSubmit) {
                        score = score * (1 - deduction);
                    }
                    scoreRecord.setScore(score);
                    scoreRecord.setResultType(QuestionResultTypeEnum.CORRECT);
                } else if (result != null) {
                    // 如果是补交，扣除一定比例的分数
                    float score = question.getScore() * 0.5f;
                    if (isLateSubmit) {
                        score = score * (1 - deduction);
                    }
                    scoreRecord.setScore(score);
                    scoreRecord.setResultType(QuestionResultTypeEnum.WRONG);
                } else {
                    scoreRecord.setScore(0f);
                    scoreRecord.setResultType(QuestionResultTypeEnum.WRONG);
                }
                
                scoreRecords.add(scoreRecord);
            }
        }
        
        // 保存得分记录
        if (!scoreRecords.isEmpty()) {
            homeworkScoreRecordService.saveBatch(scoreRecords);
        }
        
        // 记录机器批阅日志
        homeworkAnswerLogService.saveReviewStatus(homeworkInfoId, userId, HomeworkAnswerLogEnum.ROBOT_REVIEW);
        
        return Result.msgSuccess("作业提交成功");
    }
    
    @Operation(summary = "获取作业详情")
    @GetMapping("/detail/{homeworkInfoId}")
    public Result getHomeworkDetail(@PathVariable Integer homeworkInfoId) {
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        if (homeworkInfo == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        Integer userId = UserAuthUtil.getUserId();
        
        // 检查是否是该课程的学生
        JoinClass joinClass = courseClient.joinCourseByStuId(homeworkInfo.getCourseId(), userId);
        if (joinClass == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的学生");
        }
        
        // 获取作业题目
        List<Question> questions = homeworkQuestionService.getQuestionByHomeworkId(homeworkInfo.getHomeworkId());
        
        // 获取作答结果
        List<HomeworkAnswerResult> answerResults = homeworkAnswerResultService.getAnswerResultListByUserId(homeworkInfoId, userId);
        
        // 获取得分记录
        List<HomeworkScoreRecord> scoreRecords = homeworkScoreRecordService.getScoreListByUserId(homeworkInfoId, userId);
        
        // 计算总分
        AtomicReference<Float> totalScore = new AtomicReference<>(0f);
        scoreRecords.forEach(record -> totalScore.updateAndGet(v -> v + record.getScore()));
        
        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("homeworkInfo", homeworkInfo);
        result.put("questions", questions);
        result.put("answerResults", answerResults);
        result.put("scoreRecords", scoreRecords);
        result.put("totalScore", totalScore.get());
        
        return Result.success(result);
    }
} 