package com.baymax.exam.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.video.mapper.ChapterMapper;
import com.baymax.exam.video.model.Chapter;
import com.baymax.exam.video.service.IChapterService;
import com.baymax.exam.video.service.ISectionService;
import com.baymax.exam.video.vo.ChapterVO;
import com.baymax.exam.video.vo.SectionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 章节服务实现类
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements IChapterService {

    @Autowired
    private ISectionService sectionService;

    /**
     * 获取课程的所有章节（包含小节）
     *
     * @param courseId 课程ID
     * @return 章节列表
     */
    @Override
    public List<ChapterVO> getChaptersByCourseId(Integer courseId) {
        // 1. 查询课程下的所有章节
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getCourseId, courseId);
        List<Chapter> chapters = list(queryWrapper);
        
        // 2. 将章节转换为VO对象，并填充小节信息
        List<ChapterVO> chapterVOList = new ArrayList<>();
        for (Chapter chapter : chapters) {
            ChapterVO chapterVO = new ChapterVO();
            BeanUtils.copyProperties(chapter, chapterVO);
            // 获取章节下的所有小节
            chapterVO.setSections(sectionService.getSectionsByChapterId(chapter.getId())
                    .stream()
                    .map(section -> {
                        SectionVO sectionVO = new SectionVO();
                        BeanUtils.copyProperties(section, sectionVO);
                        return sectionVO;
                    })
                    .toList());
            chapterVOList.add(chapterVO);
        }
        
        return chapterVOList;
    }

    /**
     * 获取课程的所有章节（包含小节和学习进度）
     *
     * @param courseId  课程ID
     * @param studentId 学生ID
     * @return 章节列表
     */
    @Override
    public List<ChapterVO> getChaptersByCourseIdWithProgress(Integer courseId, Integer studentId) {
        // 1. 查询课程下的所有章节
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getCourseId, courseId);
        List<Chapter> chapters = list(queryWrapper);
        
        // 2. 将章节转换为VO对象，并填充小节信息和学习进度
        List<ChapterVO> chapterVOList = new ArrayList<>();
        for (Chapter chapter : chapters) {
            ChapterVO chapterVO = new ChapterVO();
            BeanUtils.copyProperties(chapter, chapterVO);
            // 获取章节下的所有小节（包含学习进度）
            chapterVO.setSections(sectionService.getSectionsByChapterIdWithProgress(chapter.getId(), studentId));
            chapterVOList.add(chapterVO);
        }
        
        return chapterVOList;
    }
} 