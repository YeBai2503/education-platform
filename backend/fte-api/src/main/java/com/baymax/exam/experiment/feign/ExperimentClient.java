package com.baymax.exam.experiment.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.experiment.vo.ExperimentSubmitVO;
import com.baymax.exam.experiment.vo.ExperimentVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 实验Feign客户端接口
 */
@FeignClient(value = "exam-experiment", contextId = "ExperimentClient", path = "/experiment/publish")
public interface ExperimentClient {
    
    /**
     * 分页查询课程下的实验列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param courseId 课程ID
     * @param type 类型：0-实验，1-项目实训，null-全部
     * @return 实验列表
     */
    @GetMapping("/course/{courseId}/list")
    Result<IPage<ExperimentVO>> pageExperimentsByCourseId(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @PathVariable Integer courseId,
            @RequestParam(required = false) Integer type);
    
    /**
     * 获取实验详情
     *
     * @param id 实验ID
     * @return 实验详情
     */
    @GetMapping("/detail/{id}")
    Result<ExperimentVO> getExperimentDetail(@PathVariable Integer id);
    
    /**
     * 统计课程下的实验数量
     *
     * @param courseId 课程ID
     * @param type 类型：0-实验，1-项目实训，null-全部
     * @return 实验数量
     */
    @GetMapping("/course/{courseId}/count")
    Result<Integer> countExperiments(@PathVariable Integer courseId, @RequestParam(required = false) Integer type);
} 