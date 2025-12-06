package com.baymax.exam.center.mapper;

import com.baymax.exam.center.model.ExamQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.center.model.Question;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 */
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
    List<Question> getQuestion(Integer examId);
}
