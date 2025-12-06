package com.baymax.exam.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.question.model.Answer;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description：回答Mapper接口
 */
@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {
} 