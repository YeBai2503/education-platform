package com.baymax.exam.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.video.mapper.SectionMapper;
import com.baymax.exam.video.model.Section;
import com.baymax.exam.video.service.IProgressService;
import com.baymax.exam.video.service.ISectionService;
import com.baymax.exam.video.vo.SectionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 小节服务实现类
 */
@Service
public class SectionServiceImpl extends ServiceImpl<SectionMapper, Section> implements ISectionService {

    @Autowired
    private IProgressService progressService;

    /**
     * 获取章节下的所有小节
     *
     * @param chapterId 章节ID
     * @return 小节列表
     */
    @Override
    public List<Section> getSectionsByChapterId(Integer chapterId) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getChapterId, chapterId);
        queryWrapper.orderByAsc(Section::getRank);
        return list(queryWrapper);
    }

    /**
     * 获取章节下的所有小节（包含学习进度）
     *
     * @param chapterId 章节ID
     * @param studentId 学生ID
     * @return 小节列表
     */
    @Override
    public List<SectionVO> getSectionsByChapterIdWithProgress(Integer chapterId, Integer studentId) {
        // 1. 获取章节下的所有小节
        List<Section> sections = getSectionsByChapterId(chapterId);
        
        // 2. 转换为VO对象，并填充学习进度
        List<SectionVO> sectionVOList = new ArrayList<>();
        for (Section section : sections) {
            SectionVO sectionVO = new SectionVO();
            BeanUtils.copyProperties(section, sectionVO);
            
            // 获取学习进度
            Integer progress = progressService.getProgress(section.getId(), studentId);
            sectionVO.setProgress(progress != null ? progress : 0);
            sectionVO.setCompleted(progress != null && progress >= 100);
            
            sectionVOList.add(sectionVO);
        }
        
        return sectionVOList;
    }

    /**
     * 获取小节详情（包含学习进度）
     *
     * @param sectionId 小节ID
     * @param studentId 学生ID
     * @return 小节详情
     */
    @Override
    public SectionVO getSectionWithProgress(Integer sectionId, Integer studentId) {
        // 1. 获取小节信息
        Section section = getById(sectionId);
        if (section == null) {
            return null;
        }
        
        // 2. 转换为VO对象，并填充学习进度
        SectionVO sectionVO = new SectionVO();
        BeanUtils.copyProperties(section, sectionVO);
        
        // 获取学习进度
        Integer progress = progressService.getProgress(sectionId, studentId);
        sectionVO.setProgress(progress != null ? progress : 0);
        sectionVO.setCompleted(progress != null && progress >= 100);
        
        return sectionVO;
    }
} 