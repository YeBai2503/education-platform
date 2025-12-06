package com.baymax.exam.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.common.core.enums.ClientIdEnum;
import com.baymax.exam.homework.enums.HomeworkAnswerLogEnum;
import com.baymax.exam.homework.mapper.HomeworkAnswerLogMapper;
import com.baymax.exam.homework.model.HomeworkAnswerLog;
import com.baymax.exam.homework.model.HomeworkInfo;
import com.baymax.exam.homework.service.IHomeworkAnswerLogService;
import com.baymax.exam.message.MessageResult;
import com.baymax.exam.message.enums.MessageCodeEnum;
import com.baymax.exam.message.feign.MessageServiceClient;
import com.baymax.exam.message.model.MessageInfo;
import com.baymax.exam.user.feign.UserClient;
import com.baymax.exam.user.model.User;
import com.baymax.exam.web.utils.UserAuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 作业作答日志 服务实现类
 * </p>
 *
 */
@Slf4j
@Service
public class HomeworkAnswerLogServiceImpl extends ServiceImpl<HomeworkAnswerLogMapper, HomeworkAnswerLog> implements IHomeworkAnswerLogService {

    @Autowired
    private UserClient userClient;
    
    @Autowired
    private MessageServiceClient messageServiceClient;

    @Override
    public void writeLog(Integer stuId, Integer classId, HomeworkInfo homeworkInfo, HomeworkAnswerLogEnum logEnum, String info) {
        HomeworkAnswerLog homeworkAnswerLog = new HomeworkAnswerLog();
        homeworkAnswerLog.setStatus(logEnum);
        homeworkAnswerLog.setStudentId(stuId);
        homeworkAnswerLog.setClassId(classId);
        homeworkAnswerLog.setHomeworkInfoId(homeworkInfo.getId());
        homeworkAnswerLog.setHomeworkId(homeworkInfo.getHomeworkId());
        homeworkAnswerLog.setInfo(info);
        save(homeworkAnswerLog);
        
        // 发送消息通知
        if (logEnum == HomeworkAnswerLogEnum.SUBMIT) {
            try {
                User user = userClient.getBaseUserInfoById(stuId);
                if (user != null) {
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setTitle("作业提交通知");
                    messageInfo.setIntroduce(user.getUsername() + " 提交了作业《" + homeworkInfo.getTitle() + "》");
                    messageInfo.setTargetId(homeworkInfo.getTeacherId());
                    messageInfo.setUserId(stuId);
                    messageInfo.setClientId(ClientIdEnum.WEB);
                    
                    // 使用MessageResult包装MessageInfo
                    MessageResult messageResult = new MessageResult();
                    messageResult.setInfo(messageInfo);
                    messageResult.setCode(MessageCodeEnum.HOMEWORK_CONSOLE_STATISTICS);
                    messageServiceClient.sendMessage(messageResult);
                }
            } catch (Exception e) {
                log.error("发送作业提交消息失败", e);
            }
        }
    }
    
    /**
     * 保存作业批阅状态
     * 
     * @param homeworkInfoId 作业信息ID
     * @param studentId 学生ID
     * @param logEnum 日志枚举
     */
    public void saveReviewStatus(Integer homeworkInfoId, Integer studentId, HomeworkAnswerLogEnum logEnum) {
        HomeworkAnswerLog homeworkAnswerLog = new HomeworkAnswerLog();
        homeworkAnswerLog.setStatus(logEnum);
        homeworkAnswerLog.setStudentId(studentId);
        homeworkAnswerLog.setHomeworkInfoId(homeworkInfoId);
        homeworkAnswerLog.setInfo(logEnum == HomeworkAnswerLogEnum.TEACHER_REVIEW ? "教师批阅" : "机器批阅");
        save(homeworkAnswerLog);
        
        // 如果是教师批阅，发送消息通知学生
        if (logEnum == HomeworkAnswerLogEnum.TEACHER_REVIEW) {
            try {
                User teacher = userClient.getBaseUserInfoById(getUserId());
                if (teacher != null) {
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setTitle("作业批阅通知");
                    messageInfo.setIntroduce("教师 " + teacher.getUsername() + " 已批阅您的作业");
                    messageInfo.setTargetId(studentId);
                    messageInfo.setUserId(getUserId());
                    messageInfo.setClientId(ClientIdEnum.WEB);
                    
                    // 使用MessageResult包装MessageInfo
                    MessageResult messageResult = new MessageResult();
                    messageResult.setInfo(messageInfo);
                    messageResult.setCode(MessageCodeEnum.HOMEWORK_CONSOLE_STATISTICS);
                    messageServiceClient.sendMessage(messageResult);
                }
            } catch (Exception e) {
                log.error("发送作业批阅消息失败", e);
            }
        }
    }
    
    /**
     * 获取学生作业日志
     * 
     * @param homeworkInfoId 作业信息ID
     * @param studentId 学生ID
     * @param logEnum 日志枚举
     * @return 作业日志
     */
    public HomeworkAnswerLog getStudentLogOne(Integer homeworkInfoId, Integer studentId, HomeworkAnswerLogEnum logEnum) {
        LambdaQueryWrapper<HomeworkAnswerLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkAnswerLog::getHomeworkInfoId, homeworkInfoId);
        queryWrapper.eq(HomeworkAnswerLog::getStudentId, studentId);
        if (logEnum != null) {
            queryWrapper.eq(HomeworkAnswerLog::getStatus, logEnum);
        }
        queryWrapper.orderByDesc(HomeworkAnswerLog::getCreatedAt);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper);
    }
    
    /**
     * 获取作业日志列表
     * 
     * @param homeworkInfoId 作业信息ID
     * @param logEnum 日志枚举
     * @return 作业日志列表
     */
    public List<HomeworkAnswerLog> getLogListByAction(Integer homeworkInfoId, HomeworkAnswerLogEnum logEnum) {
        LambdaQueryWrapper<HomeworkAnswerLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkAnswerLog::getHomeworkInfoId, homeworkInfoId);
        queryWrapper.eq(HomeworkAnswerLog::getStatus, logEnum);
        return list(queryWrapper);
    }
    
    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    private Integer getUserId() {
        try {
            return UserAuthUtil.getUserId();
        } catch (Exception e) {
            // 默认返回1，实际应该从上下文中获取
            return 1;
        }
    }
} 