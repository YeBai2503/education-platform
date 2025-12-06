package com.baymax.exam.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.center.feign.QuestionClient;
import com.baymax.exam.center.model.Question;
import com.baymax.exam.common.core.exception.ResultException;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.homework.mapper.HomeworkQuestionMapper;
import com.baymax.exam.homework.model.HomeworkQuestion;
import com.baymax.exam.homework.service.IHomeworkQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业试题关联表 服务实现类
 * </p>
 *
 */
@Slf4j
@Service
public class HomeworkQuestionServiceImpl extends ServiceImpl<HomeworkQuestionMapper, HomeworkQuestion> implements IHomeworkQuestionService {

    @Autowired
    private QuestionClient questionClient;

    @Override
    public List<Question> getQuestionByHomeworkId(Integer homeworkId) {
        LambdaQueryWrapper<HomeworkQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkQuestion::getHomeworkId, homeworkId);
        List<HomeworkQuestion> list = this.list(queryWrapper);
        
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Integer> questionIds = list.stream().map(HomeworkQuestion::getQuestionId).collect(Collectors.toList());
        Result<List<Question>> result = questionClient.getQuestionsByIds(questionIds);
        try {
            List<Question> questions = result.getResultDate();
            return questions != null ? questions : Collections.emptyList();
        } catch (ResultException e) {
            log.error("获取题目信息失败", e);
            return Collections.emptyList();
        }
    }
} 