package com.baymax.exam.experiment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.experiment.model.Experiment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 实验Mapper接口
 */
@Mapper
public interface ExperimentMapper extends BaseMapper<Experiment> {
    
    /**
     * 统计课程下的实验数量
     *
     * @param courseId 课程ID
     * @return 实验数量
     */
    @Select("SELECT COUNT(*) FROM ep_publish WHERE course_id = #{courseId}")
    int countByCourseId(@Param("courseId") Integer courseId);
} 