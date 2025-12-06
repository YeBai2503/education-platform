package com.baymax.exam.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.video.model.Progress;

/**
 * 学习进度服务接口
 */
public interface IProgressService extends IService<Progress> {
    
    /**
     * 获取学习进度
     * @param sectionId 小节ID
     * @param studentId 学生ID
     * @return 学习进度
     */
    Integer getProgress(Integer sectionId, Integer studentId);
    
    /**
     * 更新学习进度
     * @param sectionId 小节ID
     * @param studentId 学生ID
     * @param progress 学习进度
     * @return 是否更新成功
     */
    boolean updateProgress(Integer sectionId, Integer studentId, Integer progress);
} 