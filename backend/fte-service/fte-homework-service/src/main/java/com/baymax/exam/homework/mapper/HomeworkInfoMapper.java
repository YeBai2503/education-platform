package com.baymax.exam.homework.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.homework.model.HomeworkInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 作业发布信息 Mapper 接口
 * </p>
 *
 */
@Mapper
public interface HomeworkInfoMapper extends BaseMapper<HomeworkInfo> {
    @Select("SELECT ei.* FROM eh_homework_info ei " +
            "JOIN eh_homework_class ec ON ei.id = ec.homework_info_id " +
            "${ew.customSqlSegment}")
    IPage<HomeworkInfo> getStuHomeworkInfo(Page<HomeworkInfo> page, @Param("ew") QueryWrapper<HomeworkInfo> queryWrapper);
} 