package com.baymax.exam.video.feign;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.video.vo.ChapterVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 视频服务Feign客户端
 */
@FeignClient(value = "exam-video", contextId = "VideoClient", path = "/video")
public interface VideoClient {

    /**
     * 获取课程的所有章节和小节
     * @param courseId 课程ID
     * @return 章节列表
     */
    @GetMapping("/inner/course/{courseId}/chapters")
    Result<List<ChapterVO>> getCourseChapters(@PathVariable("courseId") Integer courseId);
} 