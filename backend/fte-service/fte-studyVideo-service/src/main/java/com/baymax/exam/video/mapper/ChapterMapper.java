package com.baymax.exam.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.video.model.Chapter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 章节Mapper接口
 */
@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {
} 