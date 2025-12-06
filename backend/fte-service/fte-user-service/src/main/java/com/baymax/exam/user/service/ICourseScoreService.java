package com.baymax.exam.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.user.model.CourseScore;
import com.baymax.exam.user.vo.CourseScoreVo;

import java.util.List;

/**
 * 课程评分服务接口
 */
public interface ICourseScoreService extends IService<CourseScore> {

    /**
     * 学生评分课程
     *
     * @param courseScore 课程评分
     * @return 是否成功
     */
    boolean rateCourse(CourseScore courseScore);

    /**
     * 获取课程的平均评分
     *
     * @param courseId 课程ID
     * @return 课程评分VO
     */
    CourseScoreVo getCourseTotalScore(Integer courseId);

    /**
     * 获取评分最高的课程
     *
     * @param limit 限制数量
     * @return 课程评分VO列表
     */
    List<CourseScoreVo> getTopRatedCourses(Integer limit);
    
    /**
     * 获取学生对课程的评分
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 课程评分
     */
    CourseScore getStudentCourseScore(Integer courseId, Integer studentId);
} 