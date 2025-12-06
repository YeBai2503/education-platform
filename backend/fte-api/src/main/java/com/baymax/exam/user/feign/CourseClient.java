package com.baymax.exam.user.feign;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.user.model.Courses;
import com.baymax.exam.user.model.JoinClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2022/12/1 10:07
 * @description：
 * @modified By：
 * @version:
 */
@FeignClient(value = "exam-user",contextId = "CourseClient",path = "/courses")
public interface CourseClient {
    @GetMapping("/findCourse")
    public Courses findCourse(@RequestParam Integer courseId);

    @GetMapping("/joinCourseByStuId")
    public JoinClass joinCourseByStuId(@RequestParam Integer courseId, @RequestParam Integer stuId);

    @GetMapping("/getInfo/{classId}")
    public Result<Courses> getCourseByClassId(@PathVariable Integer classId);

    @GetMapping("/getCourseStudentCount")
    public Result<Integer> getCourseStudentCount(@RequestParam Integer courseId);

    @GetMapping("/getStudentAllCourses")
    public List<Integer> getStudentAllCourses(@RequestParam Integer studentId);
}