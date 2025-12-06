package com.baymax.exam.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.homework.mapper.HomeworkScoreRecordMapper;
import com.baymax.exam.homework.model.HomeworkScoreRecord;
import com.baymax.exam.homework.service.IHomeworkScoreRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 作业得分记录 服务实现类
 * </p>
 *
 */
@Service
public class HomeworkScoreRecordServiceImpl extends ServiceImpl<HomeworkScoreRecordMapper, HomeworkScoreRecord> implements IHomeworkScoreRecordService {

    private List<HomeworkScoreRecord> getScoreList(int homeworkInfoId, Integer userId) {
        LambdaQueryWrapper<HomeworkScoreRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkScoreRecord::getHomeworkInfoId, homeworkInfoId);
        if(userId != null) {
            queryWrapper.eq(HomeworkScoreRecord::getUserId, userId);
        }
        return list(queryWrapper);
    }
    
    @Override
    public List<HomeworkScoreRecord> getScoreListByHomeworkInfoId(int homeworkInfoId) {
        return getScoreList(homeworkInfoId, null);
    }
    
    @Override
    public List<HomeworkScoreRecord> getScoreListByClassId(int homeworkInfoId, int classId) {
        LambdaQueryWrapper<HomeworkScoreRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkScoreRecord::getHomeworkInfoId, homeworkInfoId);
        queryWrapper.eq(HomeworkScoreRecord::getClassId, classId);
        return list(queryWrapper);
    }
    
    @Override
    public List<HomeworkScoreRecord> getScoreListByUserId(int homeworkInfoId, int userId) {
        return getScoreList(homeworkInfoId, userId);
    }
} 