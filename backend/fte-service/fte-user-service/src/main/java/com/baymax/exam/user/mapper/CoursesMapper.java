package com.baymax.exam.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baymax.exam.user.model.Courses;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.user.vo.CourseInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程信息 Mapper 接口
 * </p>
 *
 * @author baymax
 * @since 2022-10-11
 */
@Mapper
public interface CoursesMapper extends BaseMapper<Courses> {
    IPage<CourseInfoVo> getCourseList(IPage<CourseInfoVo> page, QueryWrapper<CourseInfoVo> queryWrapper,Boolean isStudent);

    Courses getCourseByClassId(Integer classId);

    CourseInfoVo getCourseInfo(Integer id);
    
    IPage<CourseInfoVo> searchPublicCourses(IPage<CourseInfoVo> page, String keyword);
    
    /**
     * 根据班级ID列表获取对应的课程ID列表
     * @param classIds 班级ID列表
     * @return 课程ID列表
     */
    @Select("<script>SELECT DISTINCT course_id FROM ec_classes WHERE id IN " +
            "<foreach item='item' collection='classIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<Integer> getCoursesIdsByClassIds(@Param("classIds") List<Integer> classIds);
}
