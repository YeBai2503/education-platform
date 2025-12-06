package com.baymax.exam.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.question.model.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description：评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
} 