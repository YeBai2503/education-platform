package com.baymax.exam.center.controller;

import com.baymax.exam.center.enums.ExamAnswerLogEnum;
import com.baymax.exam.center.enums.QuestionResultTypeEnum;
import com.baymax.exam.center.enums.QuestionTypeEnum;
import com.baymax.exam.center.model.*;
import com.baymax.exam.center.service.impl.*;
import com.baymax.exam.center.utils.ExamRedisKey;
import com.baymax.exam.center.vo.ExamAnswerInfoVo;
import com.baymax.exam.center.vo.QuestionInfoVo;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.redis.utils.RedisUtils;
import com.baymax.exam.user.feign.ClassesClient;
import com.baymax.exam.user.feign.CourseClient;
import com.baymax.exam.user.feign.JoinClassClient;
import com.baymax.exam.user.feign.UserClient;
import com.baymax.exam.user.model.Classes;
import com.baymax.exam.user.model.JoinClass;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.baymax.exam.center.interceptor.ExamCenterInterceptor.EXAM_INFO_KEY;

/**
 * 缓存处理建议
 *
 * @description：考试中心
 * @modified By：
 * @version:
 */
@Slf4j
@Validated
@Tag(name = "考试中心")
@RestController
@RequestMapping("/exam-center/{examInfoId}")
public class ExamCenterController {

    @Autowired
    ExamQuestionServiceImpl examQuestionService;
    @Autowired
    QuestionItemServiceImpl questionItemService;
    @Autowired
    ExamCenterServiceImpl examCenterService;
    @Autowired
    ExamAnswerResultServiceImpl answerResultService;
    @Autowired
    ExamScoreRecordServiceImpl scoreRecordService;

    @Autowired
    UserClient userClient;
    @Autowired
    HttpServletRequest request;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    ExamAnswerLogServiceImpl examAnswerLogService;

    @Autowired
    ClassesClient classesClient;



    @Operation(summary = "开始考试")
    @GetMapping("/start")
    public Result startExam(@PathVariable Integer examInfoId){
        //FIXME:考试题目/选项，放入缓存。这里只存题目序号
        Integer userId = UserAuthUtil.getUserId();
        //获取考试信息
        ExamInfo examInfo = (ExamInfo) request.getAttribute(EXAM_INFO_KEY);
        //获取考试题目
        List<QuestionInfoVo> cacheQuestionsInfo = examCenterService.getCacheQuestionsInfo(examInfoId, examInfo.getExamId());
        //获取个人题目序号
        String key = ExamRedisKey.examStudentQuestionsInfoKey(examInfoId, userId);
        if(redisUtils.hasKey(key)){
            Map<String, Integer> questionOrders= redisUtils.getCacheMap(key);
            //还原题目顺序
            cacheQuestionsInfo.sort((o1, o2) -> {
                Integer w1=questionOrders.get(o1.getId().toString());
                Integer w2=questionOrders.get(o2.getId().toString());
                return w1.compareTo(w2);
            });
        }else{
            if(examInfo.getQuestionDisorder()){
                //打乱集合
                Collections.shuffle(cacheQuestionsInfo);
                //保存题目顺序
                final Map<String, Integer> orderWight = new HashMap<>();
                for (int i = 0; i < cacheQuestionsInfo.size(); i++) {
                    orderWight.put(cacheQuestionsInfo.get(i).getId().toString(),i);
                }
                redisUtils.setCacheMap(key,orderWight);
            }
        }
        //打乱题目选项
        if(examInfo.getOptionDisorder()){
            cacheQuestionsInfo.stream().forEach(q->{
                final QuestionTypeEnum type = q.getType();
                if(type==QuestionTypeEnum.MULTIPLE_CHOICE||type==QuestionTypeEnum.SIGNAL_CHOICE){
                    Collections.shuffle(q.getOptions());
                }
            });
        }
        String redisAnswerKey= ExamRedisKey.examStudentAnswerKey(examInfoId,userId);
        if(redisUtils.hasKey(redisAnswerKey)){
            //第n次访问，整合自己的答案
            Map<String,Map<Integer,String>> answerList = redisUtils.getCacheMap(redisAnswerKey);
            cacheQuestionsInfo.stream().forEach(q->{
                //得到个人答案
                Map<Integer, String> questionAnswer = answerList.get(q.getId().toString());
                q.getOptions().forEach(questionItem -> {
                    if(questionAnswer!=null&&questionAnswer.containsKey(questionItem.getId())){
                        String answer = questionAnswer.get(questionItem.getId());
                        questionItem.setAnswer(answer);
                    }else{
                        //去除答案
                        questionItem.setAnswer(null);
                    }
                });
            });
        }else{
            //第一次访问，清除所有答案
            cacheQuestionsInfo.stream().forEach(q->{
                q.getOptions().forEach(questionItem -> {
                    questionItem.setAnswer(null);
                });
            });
        }

        //2.题目类型分类
        //题目类型排序
        TreeMap<QuestionTypeEnum, List<QuestionInfoVo>> questionOrderGroup= examCenterService.getGroupQuestionList(cacheQuestionsInfo);
        examAnswerLogService.writeLog(userId,null,examInfo,ExamAnswerLogEnum.START,UserAuthUtil.getUserIp());
        Map<String,Object> result=new HashMap<>();
        result.put("examInfo",examInfo);
        result.put("systemTime",LocalDateTime.now());
        result.put("questionList",questionOrderGroup);
        return  Result.success(result);
    }

    //就是从Redis中获取该学生指定题目之前的做题记录
    @Operation(summary = "学生答案一致性确认")
    @GetMapping("/question/{questionId}")
    public Result option(@PathVariable Integer examInfoId, @PathVariable Integer questionId){
        //判断改题目是否在考试中
        Integer userId = UserAuthUtil.getUserId();
        //1.缓存获取答案
        String redisAnswerKey = ExamRedisKey.examStudentAnswerKey(examInfoId,userId);
        
        // 检查Redis中是否存在答案数据
        if (!redisUtils.hasKey(redisAnswerKey)) {
            return Result.success(null);
        }
        
        // 添加try-catch块处理反序列化异常
        try {
            Map<String,Map<Integer,String>> answerList = redisUtils.getCacheMap(redisAnswerKey);
            //3.获取该题的作答
            Map<Integer, String> answer = answerList != null ? answerList.get(questionId.toString()) : null;
            return Result.success(answer);
        } catch (Exception e) {
            // 记录错误日志
            log.error("Redis数据反序列化失败: {}", e.getMessage());
            // 删除可能损坏的Redis数据
            redisUtils.deleteObject(redisAnswerKey);
            // 返回空结果
            return Result.success(null);
        }
    }
    @Operation(summary = "提交答案")
    @PostMapping("/answer/{questionId}")
    public Result answer(@PathVariable Integer examInfoId, @RequestBody Map<Integer,String> answerResult, @PathVariable String questionId){
        Integer userId = UserAuthUtil.getUserId();
        String redisAnswerKey = ExamRedisKey.examStudentAnswerKey(examInfoId,userId);
        
        try {
            // 1. 先保存扁平化的数据作为备份
            for (Map.Entry<Integer, String> entry : answerResult.entrySet()) {
                String flatKey = String.format("exam:flat:%d:%d:%s:%d", 
                                             examInfoId, userId, questionId, entry.getKey());
                redisUtils.setCacheObject(flatKey, entry.getValue());
            }
            
            // 2. 尝试保存原始结构（为了兼容现有代码）
            Map<String,Map<Integer,String>> result;
            if (redisUtils.hasKey(redisAnswerKey)){
                result = redisUtils.getCacheMap(redisAnswerKey);
                if (result == null) {
                    result = new HashMap<>();
                }
            } else {
                result = new HashMap<>();
            }
            result.put(questionId, answerResult);
            redisUtils.setCacheMap(redisAnswerKey, result);
            
            log.info("提交答案：" + result);
            return Result.success(answerResult.size());
        } catch (Exception e) {
            log.error("提交答案失败: {}", e.getMessage());
            // 出错时尝试从扁平化数据恢复
            try {
                Map<String,Map<Integer,String>> recoveredData = new HashMap<>();
                Map<Integer,String> recoveredAnswers = new HashMap<>();
                
                // 查找所有该题目的扁平化数据
                String pattern = String.format("exam:flat:%d:%d:%s:*", examInfoId, userId, questionId);
                for (String key : redisUtils.keys(pattern)) {
                    // 从键中提取选项ID
                    String[] parts = key.split(":");
                    Integer optionId = Integer.parseInt(parts[parts.length-1]);
                    String answer = redisUtils.getCacheObject(key);
                    recoveredAnswers.put(optionId, answer);
                }
                
                if (!recoveredAnswers.isEmpty()) {
                    recoveredData.put(questionId, recoveredAnswers);
                    redisUtils.setCacheMap(redisAnswerKey, recoveredData);
                    log.info("从扁平化数据恢复答案成功");
                }
            } catch (Exception ex) {
                log.error("恢复答案失败: {}", ex.getMessage());
            }
            
            return Result.success(answerResult.size());
        }
    }
    @Operation(summary = "考试行为")
    @PostMapping("/action")
    public Result action(@PathVariable Integer examInfoId,@RequestBody ExamAnswerLog answerLog){
        ExamInfo examInfo = (ExamInfo) request.getAttribute(EXAM_INFO_KEY);
        final Boolean isMonitor = examInfo.getIsMonitor();
        if(isMonitor){
            Integer userId = UserAuthUtil.getUserId();
            examAnswerLogService.writeLog(userId,null,examInfo,answerLog.getStatus(),answerLog.getInfo());
        }
        //放入缓存，提交答案的时候在存到数据库
        return Result.success("提交成功");
    }
    @Operation(summary = "交卷")
    @PostMapping("/submit")
    public Result submit(@PathVariable Integer examInfoId){
        ExamInfo examInfo = (ExamInfo) request.getAttribute(EXAM_INFO_KEY);
        //放入缓存，提交答案的时候在存到数据库
        int userId = UserAuthUtil.getUserId();
        submit(userId,examInfo);
        return Result.success("交卷成功",null);
    }
    @Async
    public void submit(int userId, ExamInfo examInfo){
        try {
            //提交记录
            //获取题目
            Classes classes = classesClient.getClassByUserId(examInfo.getCourseId(), userId);
            examAnswerLogService.writeLog(userId, classes.getId(), examInfo, ExamAnswerLogEnum.SUBMIT, UserAuthUtil.getUserIp());
            List<QuestionInfoVo> questionsInfo = examCenterService.getCacheQuestionsInfo(examInfo.getId(), examInfo.getExamId());
            String redisAnswerKey = ExamRedisKey.examStudentAnswerKey(examInfo.getId(), userId);
            List<ExamAnswerResult> answerResults = new ArrayList<>();
            
            // 尝试从原始结构获取数据
            boolean dataFromOriginal = false;
            if (redisUtils.hasKey(redisAnswerKey)) {
                try {
                    Map<String,Map<Integer,String>> results = redisUtils.getCacheMap(redisAnswerKey);
                    if (results != null) {
                        results.forEach((questionId,answers) -> {
                            answers.forEach((optionId,answer) -> {
                                ExamAnswerResult result = new ExamAnswerResult();
                                result.setUserId(userId);
                                result.setExamInfoId(examInfo.getId());
                                result.setQuestionId(Integer.valueOf(questionId));
                                result.setOptionId(optionId);
                                result.setAnswer(answer);
                                answerResults.add(result);
                            });
                        });
                        dataFromOriginal = true;
                    }
                } catch (Exception e) {
                    log.error("读取Redis答案数据失败: {}", e.getMessage());
                }
            }
            
            // 如果原始结构读取失败，尝试从扁平化数据恢复
            if (!dataFromOriginal) {
                log.info("尝试从扁平化数据恢复答案");
                String pattern = String.format("exam:flat:%d:%d:*", examInfo.getId(), userId);
                Collection<String> keys = redisUtils.keys(pattern);
                
                for (String key : keys) {
                    try {
                        // 从键中提取信息：exam:flat:examInfoId:userId:questionId:optionId
                        String[] parts = key.split(":");
                        if (parts.length >= 6) {
                            String questionId = parts[4];
                            Integer optionId = Integer.parseInt(parts[5]);
                            String answer = redisUtils.getCacheObject(key);
                            
                            ExamAnswerResult result = new ExamAnswerResult();
                            result.setUserId(userId);
                            result.setExamInfoId(examInfo.getId());
                            result.setQuestionId(Integer.valueOf(questionId));
                            result.setOptionId(optionId);
                            result.setAnswer(answer);
                            answerResults.add(result);
                        }
                    } catch (Exception e) {
                        log.error("处理扁平化数据键{}时出错: {}", key, e.getMessage());
                    }
                }
            }
            
            if (!answerResults.isEmpty()) {
                List<ExamAnswerInfoVo> examAnswerInfo = examCenterService.answerCompare(questionsInfo, answerResults);
                List<ExamAnswerResult> answerResult = examAnswerInfo.stream().flatMap(info -> info.getAnswerResult().stream()).collect(Collectors.toList());
                List<ExamScoreRecord> scoreRecords = examAnswerInfo.stream().map(result -> {
                    final ExamScoreRecord scoreRecord = result.getScoreRecord();
                    scoreRecord.setClassId(classes.getId());
                    return scoreRecord;
                }).collect(Collectors.toList());
                
                // 添加日志
                log.info("保存答案记录数: {}", answerResult.size());
                log.info("保存分数记录数: {}", scoreRecords.size());
                
                // 保存到数据库
                if (!scoreRecords.isEmpty()) {
                    scoreRecordService.saveBatch(scoreRecords);
                }
                if (!answerResult.isEmpty()) {
                    answerResultService.saveBatch(answerResult);
                }
                
                examAnswerLogService.saveReviewStatus(examInfo.getId(), userId, ExamAnswerLogEnum.ROBOT_REVIEW);
            } else {
                log.warn("用户 {} 的考试 {} 没有答案数据", userId, examInfo.getId());
            }
            
            // 清理Redis缓存
            String questionsKey = ExamRedisKey.examStudentQuestionsInfoKey(examInfo.getId(), userId);
            redisUtils.deleteObject(redisAnswerKey);
            redisUtils.deleteObject(questionsKey);
            
            // 清理扁平化数据
            String flatPattern = String.format("exam:flat:%d:%d:*", examInfo.getId(), userId);
            Collection<String> flatKeys = redisUtils.keys(flatPattern);
            if (!flatKeys.isEmpty()) {
                redisUtils.deleteObject(flatKeys);
            }
            
        } catch (Exception e) {
            log.error("提交考试答案失败: {}", e.getMessage(), e);
        }
    }
}
