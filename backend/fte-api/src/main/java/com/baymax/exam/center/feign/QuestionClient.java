package com.baymax.exam.center.feign;

import com.baymax.exam.center.model.Question;
import com.baymax.exam.center.vo.QuestionInfoVo;
import com.baymax.exam.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 题目服务Feign客户端
 *
 * @author baymax
 * @since 2023-05-12
 */
@FeignClient(value = "exam-center", contextId = "question", path = "/question")
public interface QuestionClient {

    /**
     * 通过ID获取题目
     *
     * @param id 题目ID
     * @return 题目信息
     */
    @GetMapping("/info/{id}")
    Result<Question> getQuestionById(@PathVariable("id") Integer id);
    
    /**
     * 批量获取题目信息
     *
     * @param ids 题目ID列表
     * @return 题目列表
     */
    @GetMapping("/listByIds")
    Result<List<Question>> getQuestionsByIds(@RequestParam("ids") List<Integer> ids);
} 