package com.baymax.exam.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.homework.mapper.HomeworkAnswerResultMapper;
import com.baymax.exam.homework.model.HomeworkAnswerResult;
import com.baymax.exam.homework.service.IHomeworkAnswerResultService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 作业作答结果 服务实现类
 * </p>
 *
 */
@Service
public class HomeworkAnswerResultServiceImpl extends ServiceImpl<HomeworkAnswerResultMapper, HomeworkAnswerResult> implements IHomeworkAnswerResultService {
    
    private List<HomeworkAnswerResult> getAnswerResultList(int homeworkInfoId, Integer userId) {
        LambdaQueryWrapper<HomeworkAnswerResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkAnswerResult::getHomeworkInfoId, homeworkInfoId);
        if(userId != null) {
            queryWrapper.eq(HomeworkAnswerResult::getUserId, userId);
        }
        return list(queryWrapper);
    }
    
    @Override
    public List<HomeworkAnswerResult> getAnswerResultListByHomeworkInfoId(int homeworkInfoId) {
        return getAnswerResultList(homeworkInfoId, null);
    }
    
    @Override
    public List<HomeworkAnswerResult> getAnswerResultListByUserId(int homeworkInfoId, int userId) {
        return getAnswerResultList(homeworkInfoId, userId);
    }
} 