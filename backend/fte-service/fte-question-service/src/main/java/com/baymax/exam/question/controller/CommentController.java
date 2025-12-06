package com.baymax.exam.question.controller;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.question.model.CommentDTO;
import com.baymax.exam.question.model.CommentVO;
import com.baymax.exam.question.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description：评论Controller
 */
@Tag(name = "评论管理接口", description = "评论管理相关接口")
@RestController
@RequestMapping("/question/comment")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @Operation(summary = "添加评论")
    @PostMapping("/add")
    public Result<CommentVO> addComment(@RequestBody @Validated CommentDTO commentDTO) {
        return Result.success(commentService.addComment(commentDTO));
    }
    
    @Operation(summary = "删除评论")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteComment(@PathVariable("id") Integer id) {
        return Result.success(commentService.deleteComment(id));
    }
    
    @Operation(summary = "获取问题评论列表")
    @GetMapping("/question/{questionId}")
    public Result<List<CommentVO>> getQuestionComments(@PathVariable("questionId") Integer questionId) {
        return Result.success(commentService.getQuestionComments(questionId));
    }
    
    @Operation(summary = "获取回答评论列表")
    @GetMapping("/answer/{answerId}")
    public Result<List<CommentVO>> getAnswerComments(@PathVariable("answerId") Integer answerId) {
        return Result.success(commentService.getAnswerComments(answerId));
    }
} 