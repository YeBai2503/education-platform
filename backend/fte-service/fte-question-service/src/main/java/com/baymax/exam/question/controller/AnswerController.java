package com.baymax.exam.question.controller;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.question.model.AnswerDTO;
import com.baymax.exam.question.model.AnswerVO;
import com.baymax.exam.question.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description：回答Controller
 */
@Tag(name = "回答管理接口", description = "回答管理相关接口")
@RestController
@RequestMapping("/question/answer")
@RequiredArgsConstructor
public class AnswerController {
    
    private final AnswerService answerService;
    
    @Operation(summary = "添加回答")
    @PostMapping("/add")
    public Result<AnswerVO> addAnswer(@RequestBody @Validated AnswerDTO answerDTO) {
        return Result.success(answerService.addAnswer(answerDTO));
    }
    
    @Operation(summary = "更新回答")
    @PutMapping("/update")
    public Result<Boolean> updateAnswer(@RequestBody @Validated AnswerDTO answerDTO) {
        return Result.success(answerService.updateAnswer(answerDTO));
    }
    
    @Operation(summary = "删除回答")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteAnswer(@PathVariable("id") Integer id) {
        return Result.success(answerService.deleteAnswer(id));
    }
    
    @Operation(summary = "获取回答详情")
    @GetMapping("/get/{id}")
    public Result<AnswerVO> getAnswerById(@PathVariable("id") Integer id) {
        return Result.success(answerService.getAnswerById(id));
    }
    
    @Operation(summary = "获取问题的回答列表")
    @GetMapping("/list/{questionId}")
    public Result<List<AnswerVO>> getAnswersByQuestionId(@PathVariable("questionId") Integer questionId) {
        return Result.success(answerService.getAnswersByQuestionId(questionId));
    }
    
    @Operation(summary = "采纳回答")
    @PutMapping("/accept/{answerId}/{questionId}")
    public Result<Boolean> acceptAnswer(
            @PathVariable("answerId") Integer answerId,
            @PathVariable("questionId") Integer questionId) {
        return Result.success(answerService.acceptAnswer(answerId, questionId));
    }
} 