package com.baymax.exam.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.video.mapper.ProgressMapper;
import com.baymax.exam.video.model.Progress;
import com.baymax.exam.video.service.IProgressService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 学习进度服务实现类
 */
@Service
public class ProgressServiceImpl extends ServiceImpl<ProgressMapper, Progress> implements IProgressService {

    /**
     * 获取学习进度
     *
     * @param sectionId 小节ID
     * @param studentId 学生ID
     * @return 学习进度
     */
    @Override
    public Integer getProgress(Integer sectionId, Integer studentId) {
        LambdaQueryWrapper<Progress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Progress::getSectionId, sectionId);
        queryWrapper.eq(Progress::getStudentId, studentId);
        Progress progress = getOne(queryWrapper);
        return progress != null ? progress.getProgress() : null;
    }

    /**
     * 更新学习进度
     *
     * @param sectionId 小节ID
     * @param studentId 学生ID
     * @param progress  学习进度
     * @return 是否更新成功
     */
    @Override
    public boolean updateProgress(Integer sectionId, Integer studentId, Integer progress) {
        // 查询是否已存在进度记录
        LambdaQueryWrapper<Progress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Progress::getSectionId, sectionId);
        queryWrapper.eq(Progress::getStudentId, studentId);
        Progress existingProgress = getOne(queryWrapper);
        
        if (existingProgress != null) {
            // 已存在，更新进度
            LambdaUpdateWrapper<Progress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Progress::getSectionId, sectionId);
            updateWrapper.eq(Progress::getStudentId, studentId);
            updateWrapper.set(Progress::getProgress, progress);
            updateWrapper.set(Progress::getUpdatedAt, LocalDateTime.now());
            return update(updateWrapper);
        } else {
            // 不存在，创建新记录
            Progress newProgress = new Progress();
            newProgress.setSectionId(sectionId);
            newProgress.setStudentId(studentId);
            newProgress.setProgress(progress);
            newProgress.setCreatedAt(LocalDateTime.now());
            newProgress.setUpdatedAt(LocalDateTime.now());
            return save(newProgress);
        }
    }
} 