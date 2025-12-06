package com.baymax.exam.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.video.model.Progress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习进度Mapper接口
 */
@Mapper
public interface ProgressMapper extends BaseMapper<Progress> {
} 