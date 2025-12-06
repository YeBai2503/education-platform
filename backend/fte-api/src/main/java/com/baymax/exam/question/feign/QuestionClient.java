package com.baymax.exam.question.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.question.model.AnswerDTO;
import com.baymax.exam.question.model.AnswerVO;
import com.baymax.exam.question.model.QuestionDTO;
import com.baymax.exam.question.model.QuestionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：问题中心服务Feign客户端
 */
@FeignClient(value = "exam-question", contextId = "QuestionClient", path = "/question")
public interface QuestionClient {

    /**
     * 添加问题
     *
     * @param questionDTO 问题DTO
     * @return 问题VO
     */
    @PostMapping("/add")
    Result<QuestionVO> addQuestion(@RequestBody QuestionDTO questionDTO);

    /**
     * 更新问题
     *
     * @param questionDTO 问题DTO
     * @return 是否更新成功
     */
    @PutMapping("/update")
    Result<Boolean> updateQuestion(@RequestBody QuestionDTO questionDTO);

    /**
     * 删除问题
     *
     * @param id 问题ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete/{id}")
    Result<Boolean> deleteQuestion(@PathVariable("id") Integer id);

    /**
     * 根据ID获取问题详情
     *
     * @param id 问题ID
     * @return 问题VO
     */
    @GetMapping("/get/{id}")
    Result<QuestionVO> getQuestionById(@PathVariable("id") Integer id);

    /**
     * 获取当前用户问题列表
     *
     * @return 问题VO列表
     */
    @GetMapping("/list/user")
    Result<List<QuestionVO>> getUserQuestions();

    /**
     * 根据课程ID获取问题列表
     *
     * @param courseId 课程ID
     * @return 问题VO列表
     */
    @GetMapping("/list/course/{courseId}")
    Result<List<QuestionVO>> getQuestionsByCourseId(@PathVariable("courseId") Integer courseId);

    /**
     * 分页获取课程问题
     *
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页问题VO
     */
    @GetMapping("/page/course/{courseId}/{page}/{size}")
    Result<Page<QuestionVO>> pageQuestionsByCourseId(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size);
    
    /**
     * 添加回答
     *
     * @param answerDTO 回答DTO
     * @return 回答VO
     */
    @PostMapping("/answer/add")
    Result<AnswerVO> addAnswer(@RequestBody AnswerDTO answerDTO);
    
    /**
     * 获取问题的回答列表
     *
     * @param questionId 问题ID
     * @return 回答VO列表
     */
    @GetMapping("/answer/list/{questionId}")
    Result<List<AnswerVO>> getAnswersByQuestionId(@PathVariable("questionId") Integer questionId);
}