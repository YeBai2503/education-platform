package com.baymax.exam.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.question.model.Question;
import com.baymax.exam.question.model.QuestionDTO;
import com.baymax.exam.question.model.QuestionVO;

import java.util.List;

/**
 * @description：问题Service接口
 */
public interface QuestionService extends IService<Question> {
    
    /**
     * 添加问题
     *
     * @param questionDTO 问题DTO
     * @return 问题VO
     */
    QuestionVO addQuestion(QuestionDTO questionDTO);
    
    /**
     * 更新问题
     *
     * @param questionDTO 问题DTO
     * @return 是否更新成功
     */
    boolean updateQuestion(QuestionDTO questionDTO);
    
    /**
     * 删除问题
     *
     * @param id 问题ID
     * @return 是否删除成功
     */
    boolean deleteQuestion(Integer id);
    
    /**
     * 根据ID获取问题详情
     *
     * @param id 问题ID
     * @return 问题VO
     */
    QuestionVO getQuestionById(Integer id);
    
    /**
     * 获取当前用户问题列表
     *
     * @return 问题VO列表
     */
    List<QuestionVO> getUserQuestions();
    
    /**
     * 根据课程ID获取问题列表
     *
     * @param courseId 课程ID
     * @return 问题VO列表
     */
    List<QuestionVO> getQuestionsByCourseId(Integer courseId);
    
    /**
     * 分页获取课程问题
     *
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页问题VO
     */
    Page<QuestionVO> pageQuestionsByCourseId(Integer courseId, Integer page, Integer size);
    
    /**
     * 更新问题的回答数量
     *
     * @param questionId 问题ID
     * @param increment 增量，正数表示增加，负数表示减少
     * @return 是否更新成功
     */
    boolean updateQuestionAnswerCount(Integer questionId, Integer increment);
} 