package com.baymax.exam.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.center.model.Question;
import com.baymax.exam.homework.model.HomeworkQuestion;

import java.util.List;

/**
 * <p>
 * 作业试题关联表 服务接口
 * </p>
 *
 */
public interface IHomeworkQuestionService extends IService<HomeworkQuestion> {
    /**
     * 根据作业ID获取题目列表
     * @param homeworkId
     * @return
     */
    List<Question> getQuestionByHomeworkId(Integer homeworkId);
} 