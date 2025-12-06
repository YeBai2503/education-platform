package com.baymax.exam.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.homework.model.HomeworkAnswerResult;

import java.util.List;

/**
 * <p>
 * 作业作答结果 服务类
 * </p>
 *
 */
public interface IHomeworkAnswerResultService extends IService<HomeworkAnswerResult> {
    /**
     * 根据作业信息ID获取作答结果列表
     * @param homeworkInfoId 作业信息ID
     * @return 作答结果列表
     */
    List<HomeworkAnswerResult> getAnswerResultListByHomeworkInfoId(int homeworkInfoId);
    
    /**
     * 根据作业信息ID和用户ID获取作答结果列表
     * @param homeworkInfoId 作业信息ID
     * @param userId 用户ID
     * @return 作答结果列表
     */
    List<HomeworkAnswerResult> getAnswerResultListByUserId(int homeworkInfoId, int userId);
} 