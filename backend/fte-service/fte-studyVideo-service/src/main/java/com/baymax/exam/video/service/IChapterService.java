package com.baymax.exam.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.video.model.Chapter;
import com.baymax.exam.video.vo.ChapterVO;

import java.util.List;

/**
 * 章节服务接口
 */
public interface IChapterService extends IService<Chapter> {
    
    /**
     * 获取课程的所有章节（包含小节）
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<ChapterVO> getChaptersByCourseId(Integer courseId);
    
    /**
     * 获取课程的所有章节（包含小节和学习进度）
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 章节列表
     */
    List<ChapterVO> getChaptersByCourseIdWithProgress(Integer courseId, Integer studentId);
} 