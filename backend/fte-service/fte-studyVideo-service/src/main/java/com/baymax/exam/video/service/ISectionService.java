package com.baymax.exam.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.video.model.Section;
import com.baymax.exam.video.vo.SectionVO;

import java.util.List;

/**
 * 小节服务接口
 */
public interface ISectionService extends IService<Section> {
    
    /**
     * 获取章节下的所有小节
     * @param chapterId 章节ID
     * @return 小节列表
     */
    List<Section> getSectionsByChapterId(Integer chapterId);
    
    /**
     * 获取章节下的所有小节（包含学习进度）
     * @param chapterId 章节ID
     * @param studentId 学生ID
     * @return 小节列表
     */
    List<SectionVO> getSectionsByChapterIdWithProgress(Integer chapterId, Integer studentId);
    
    /**
     * 获取小节详情（包含学习进度）
     * @param sectionId 小节ID
     * @param studentId 学生ID
     * @return 小节详情
     */
    SectionVO getSectionWithProgress(Integer sectionId, Integer studentId);
} 