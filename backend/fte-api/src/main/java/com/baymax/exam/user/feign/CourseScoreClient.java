package com.baymax.exam.user.feign;

import com.baymax.exam.user.vo.CourseScoreVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 课程评分Feign客户端
 */
@FeignClient(value = "exam-user", contextId = "CourseScoreClient", path = "/course-score")
public interface CourseScoreClient {

    /**
     * 获取课程的平均评分
     *
     * @param courseId 课程ID
     * @return 课程评分VO
     */
    @GetMapping("/inner/course/{courseId}")
    CourseScoreVo getCourseTotalScore(@PathVariable Integer courseId);
    
    /**
     * 获取评分最高的课程
     *
     * @param limit 限制数量
     * @return 课程评分VO列表
     */
    @GetMapping("/top/{limit}")
    List<CourseScoreVo> getTopRatedCourses(@PathVariable Integer limit);
} 