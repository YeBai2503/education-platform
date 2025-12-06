package com.baymax.exam.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.user.model.CourseScore;
import com.baymax.exam.user.vo.CourseScoreVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程评分Mapper接口
 */
@Mapper
public interface CourseScoreMapper extends BaseMapper<CourseScore> {

    /**
     * 获取课程的平均评分
     *
     * @param courseId 课程ID
     * @return 课程评分VO
     */
    CourseScoreVo getCourseTotalScore(@Param("courseId") Integer courseId);

    /**
     * 获取评分最高的课程
     *
     * @param limit 限制数量
     * @return 课程评分VO列表
     */
    List<CourseScoreVo> getTopRatedCourses(@Param("limit") Integer limit);
} 