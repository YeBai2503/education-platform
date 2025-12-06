package com.baymax.exam.experiment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.experiment.model.ExperimentSubmit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 实验提交Mapper接口
 */
@Mapper
public interface ExperimentSubmitMapper extends BaseMapper<ExperimentSubmit> {
    
    /**
     * 统计实验的提交数量
     *
     * @param experimentId 实验ID
     * @return 提交数量
     */
    @Select("SELECT COUNT(*) FROM ep_submit WHERE experiment_id = #{experimentId}")
    int countByExperimentId(@Param("experimentId") Integer experimentId);
    
    /**
     * 检查学生是否已提交实验
     *
     * @param experimentId 实验ID
     * @param studentId 学生ID
     * @return 提交数量
     */
    @Select("SELECT COUNT(*) FROM ep_submit WHERE experiment_id = #{experimentId} AND student_id = #{studentId}")
    int checkStudentSubmitted(@Param("experimentId") Integer experimentId, @Param("studentId") Integer studentId);
} 