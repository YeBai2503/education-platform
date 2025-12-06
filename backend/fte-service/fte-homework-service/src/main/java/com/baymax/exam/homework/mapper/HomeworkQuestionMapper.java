package com.baymax.exam.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.homework.model.HomeworkQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 作业试题关联表 Mapper 接口
 * </p>
 *
 */
@Mapper
public interface HomeworkQuestionMapper extends BaseMapper<HomeworkQuestion> {

} 