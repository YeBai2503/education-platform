package com.baymax.exam.homework.feign;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.homework.model.HomeworkInfo;
import com.baymax.exam.homework.model.HomeworkPaper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 作业服务Feign客户端
 *
 * @author baymax
 * @since 2023-05-12
 */
@FeignClient(value = "exam-homework", contextId = "homework", path = "/homework")
public interface HomeworkServiceClient {

    /**
     * 获取作业信息
     *
     * @param homeworkId 作业ID
     * @return 作业信息
     */
    @GetMapping("/homework-paper/info/{homeworkId}")
    Result<HomeworkPaper> getHomeworkInfo(@PathVariable("homeworkId") Integer homeworkId);

    /**
     * 获取作业发布信息
     *
     * @param homeworkInfoId 作业发布ID
     * @return 作业发布信息
     */
    @GetMapping("/homework-info/info/{homeworkInfoId}")
    Result<HomeworkInfo> getHomeworkPublishInfo(@PathVariable("homeworkInfoId") Integer homeworkInfoId);
} 