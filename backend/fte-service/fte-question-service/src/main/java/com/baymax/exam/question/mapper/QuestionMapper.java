package com.baymax.exam.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.question.model.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description：问题Mapper接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
} 