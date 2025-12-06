package com.baymax.exam.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.question.model.Answer;
import com.baymax.exam.question.model.AnswerDTO;
import com.baymax.exam.question.model.AnswerVO;

import java.util.List;

/**
 * @description：回答Service接口
 */
public interface AnswerService extends IService<Answer> {
    
    /**
     * 添加回答
     *
     * @param answerDTO 回答DTO
     * @return 回答VO
     */
    AnswerVO addAnswer(AnswerDTO answerDTO);
    
    /**
     * 更新回答
     *
     * @param answerDTO 回答DTO
     * @return 是否更新成功
     */
    boolean updateAnswer(AnswerDTO answerDTO);
    
    /**
     * 删除回答
     *
     * @param id 回答ID
     * @return 是否删除成功
     */
    boolean deleteAnswer(Integer id);
    
    /**
     * 获取回答详情
     *
     * @param id 回答ID
     * @return 回答VO
     */
    AnswerVO getAnswerById(Integer id);
    
    /**
     * 获取问题的回答列表
     *
     * @param questionId 问题ID
     * @return 回答VO列表
     */
    List<AnswerVO> getAnswersByQuestionId(Integer questionId);
    
    /**
     * 采纳回答
     *
     * @param answerId 回答ID
     * @param questionId 问题ID
     * @return 是否采纳成功
     */
    boolean acceptAnswer(Integer answerId, Integer questionId);
} 