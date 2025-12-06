package com.baymax.exam.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.question.model.Comment;
import com.baymax.exam.question.model.CommentDTO;
import com.baymax.exam.question.model.CommentVO;

import java.util.List;

/**
 * @description：评论Service接口
 */
public interface CommentService extends IService<Comment> {
    
    /**
     * 添加评论
     *
     * @param commentDTO 评论DTO
     * @return 评论VO
     */
    CommentVO addComment(CommentDTO commentDTO);
    
    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 是否删除成功
     */
    boolean deleteComment(Integer id);
    
    /**
     * 获取问题评论列表
     *
     * @param questionId 问题ID
     * @return 评论VO列表
     */
    List<CommentVO> getQuestionComments(Integer questionId);
    
    /**
     * 获取回答评论列表
     *
     * @param answerId 回答ID
     * @return 评论VO列表
     */
    List<CommentVO> getAnswerComments(Integer answerId);
} 