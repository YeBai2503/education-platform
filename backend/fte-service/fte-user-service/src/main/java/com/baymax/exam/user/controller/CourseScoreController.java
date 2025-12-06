package com.baymax.exam.user.controller;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.user.model.CourseScore;
import com.baymax.exam.user.model.JoinClass;
import com.baymax.exam.user.service.IJoinClassService;
import com.baymax.exam.user.service.impl.CourseScoreServiceImpl;
import com.baymax.exam.user.vo.CourseScoreVo;
import com.baymax.exam.web.annotation.Inner;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程评分控制器
 */
@Slf4j
@Validated
@Tag(name = "课程评分")
@RestController
@RequestMapping("/course-score")
public class CourseScoreController {

    @Autowired
    private CourseScoreServiceImpl courseScoreService;
    
    @Autowired
    private IJoinClassService joinClassService;

    @Operation(summary = "学生评分课程")
    @PostMapping("/rate")
    public Result<Boolean> rateCourse(@RequestBody @Validated CourseScore courseScore) {
        Integer userId = UserAuthUtil.getUserId();
        if (userId == null) {
            return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }
        
        // 设置学生ID
        courseScore.setStudentId(userId);
        
        // 验证学生是否加入了该课程
        JoinClass joinClass = joinClassService.getJoinByCourseId(userId, courseScore.getCourseId());
        if (joinClass == null) {
            return Result.msgWaring("您尚未加入该课程，无法评分");
        }
        
        // 验证评分是否在1-5之间
        if (!isValidScore(courseScore.getVideoScore()) || 
            !isValidScore(courseScore.getExamScore()) || 
            !isValidScore(courseScore.getExperimentScore()) || 
            !isValidScore(courseScore.getProjectScore()) || 
            !isValidScore(courseScore.getHomeworkScore())) {
            return Result.msgWaring("评分必须在1-5之间");
        }
        
        boolean success = courseScoreService.rateCourse(courseScore);
        return success ? Result.msgSuccess("评分成功") : Result.msgWaring("评分失败");
    }

    @Operation(summary = "获取课程的平均评分")
    @GetMapping("/course/{courseId}")
    public Result<CourseScoreVo> getCourseTotalScore(@PathVariable Integer courseId) {
        CourseScoreVo scoreVo = courseScoreService.getCourseTotalScore(courseId);
        return Result.success(scoreVo);
    }

    @Operation(summary = "获取评分最高的课程")
    @GetMapping("/top/{limit}")
    public Result<List<CourseScoreVo>> getTopRatedCourses(@PathVariable Integer limit) {
        List<CourseScoreVo> topCourses = courseScoreService.getTopRatedCourses(limit);
        return Result.success(topCourses);
    }

    @Operation(summary = "获取学生对课程的评分")
    @GetMapping("/student")
    public Result<CourseScore> getStudentCourseScore(@RequestParam Integer courseId) {
        Integer userId = UserAuthUtil.getUserId();
        if (userId == null) {
            return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }
        
        CourseScore courseScore = courseScoreService.getStudentCourseScore(courseId, userId);
        return Result.success(courseScore);
    }
    
    @Inner
    @Operation(summary = "内部接口：获取课程的平均评分")
    @GetMapping("/inner/course/{courseId}")
    public CourseScoreVo getCourseTotalScoreInner(@PathVariable Integer courseId) {
        return courseScoreService.getCourseTotalScore(courseId);
    }
    
    /**
     * 验证评分是否有效（1-5分）
     */
    private boolean isValidScore(Integer score) {
        return score != null && score >= 1 && score <= 5;
    }
} 