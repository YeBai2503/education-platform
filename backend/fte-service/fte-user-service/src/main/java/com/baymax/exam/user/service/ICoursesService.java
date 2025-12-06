package com.baymax.exam.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baymax.exam.user.model.Courses;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.user.vo.CourseInfoVo;

/**
 * <p>
 * 课程信息 服务类
 * </p>
 *

 */
public interface ICoursesService extends IService<Courses> {
    /**
     * 更新课程
     *
     * @param courses 课程
     * @return boolean
     */
    boolean updateCourse(Courses courses);

    /**
     * 获取课程信息
     *
     * @param id id
     * @return {@link CourseInfoVo}
     */
    CourseInfoVo getCourseInfo(Integer id);

    /**
     * 获得课程列表
     *
     * @param userId      用户id
     * @param isStudent   是学生?
     * @param pageSize    页面大小
     * @param currentPage 当前页面
     * @param status      状态
     * @return {@link IPage}<{@link CourseInfoVo}>
     */
    public IPage<CourseInfoVo> getCourseList(Integer userId,Integer status,Boolean isStudent, long currentPage, long pageSize);
    
    /**
     * 搜索公开课程
     *
     * @param keyword     搜索关键词，可以为空
     * @param currentPage 当前页码
     * @param pageSize    每页大小
     * @return 课程信息分页列表
     */
    IPage<CourseInfoVo> searchPublicCourses(String keyword, long currentPage, long pageSize);
}
