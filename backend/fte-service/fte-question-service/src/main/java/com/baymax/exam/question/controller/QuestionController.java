package com.baymax.exam.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.question.model.QuestionDTO;
import com.baymax.exam.question.model.QuestionVO;
import com.baymax.exam.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description：问题Controller
 */
@Tag(name = "问题管理接口", description = "问题管理相关接口")
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {
    
    private final QuestionService questionService;
    
    @Operation(summary = "添加问题")
    @PostMapping("/add")
    public Result<QuestionVO> addQuestion(@RequestBody @Validated QuestionDTO questionDTO) {
        return Result.success(questionService.addQuestion(questionDTO));
    }
    
    @Operation(summary = "更新问题")
    @PutMapping("/update")
    public Result<Boolean> updateQuestion(@RequestBody @Validated QuestionDTO questionDTO) {
        return Result.success(questionService.updateQuestion(questionDTO));
    }
    
    @Operation(summary = "删除问题")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteQuestion(@PathVariable("id") Integer id) {
        return Result.success(questionService.deleteQuestion(id));
    }
    
    @Operation(summary = "根据ID获取问题详情")
    @GetMapping("/get/{id}")
    public Result<QuestionVO> getQuestionById(@PathVariable("id") Integer id) {
        return Result.success(questionService.getQuestionById(id));
    }
    
    @Operation(summary = "获取当前用户问题列表")
    @GetMapping("/list/user")
    public Result<List<QuestionVO>> getUserQuestions() {
        return Result.success(questionService.getUserQuestions());
    }
    
    @Operation(summary = "根据课程ID获取问题列表")
    @GetMapping("/list/course/{courseId}")
    public Result<List<QuestionVO>> getQuestionsByCourseId(@PathVariable("courseId") Integer courseId) {
        return Result.success(questionService.getQuestionsByCourseId(courseId));
    }
    
    @Operation(summary = "分页获取课程问题")
    @GetMapping("/page/course/{courseId}/{page}/{size}")
    public Result<Page<QuestionVO>> pageQuestionsByCourseId(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        return Result.success(questionService.pageQuestionsByCourseId(courseId, page, size));
    }
} 