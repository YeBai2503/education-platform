package com.baymax.exam.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.homework.model.HomeworkScoreRecord;

import java.util.List;

/**
 * <p>
 * 作业得分记录 服务类
 * </p>
 *
 */
public interface IHomeworkScoreRecordService extends IService<HomeworkScoreRecord> {
    /**
     * 根据作业信息ID获取得分记录列表
     * @param homeworkInfoId 作业信息ID
     * @return 得分记录列表
     */
    List<HomeworkScoreRecord> getScoreListByHomeworkInfoId(int homeworkInfoId);
    
    /**
     * 根据作业信息ID和班级ID获取得分记录列表
     * @param homeworkInfoId 作业信息ID
     * @param classId 班级ID
     * @return 得分记录列表
     */
    List<HomeworkScoreRecord> getScoreListByClassId(int homeworkInfoId, int classId);
    
    /**
     * 根据作业信息ID和用户ID获取得分记录列表
     * @param homeworkInfoId 作业信息ID
     * @param userId 用户ID
     * @return 得分记录列表
     */
    List<HomeworkScoreRecord> getScoreListByUserId(int homeworkInfoId, int userId);
} 