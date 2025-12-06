package com.baymax.exam.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.homework.enums.HomeworkAnswerLogEnum;
import com.baymax.exam.homework.model.HomeworkAnswerLog;
import com.baymax.exam.homework.model.HomeworkInfo;

/**
 * <p>
 * 作业作答日志 服务类
 * </p>
 *
 */
public interface IHomeworkAnswerLogService extends IService<HomeworkAnswerLog> {
    /**
     * 写日志
     *
     * @param stuId           学生id
     * @param classId         班级id
     * @param homeworkInfo    作业信息
     * @param logEnum         日志枚举
     * @param info            信息
     */
    void writeLog(Integer stuId, Integer classId, HomeworkInfo homeworkInfo, HomeworkAnswerLogEnum logEnum, String info);
} 