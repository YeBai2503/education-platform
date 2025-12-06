package com.baymax.exam.file.feign;

import com.baymax.exam.common.core.result.PageResult;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.file.model.Knowledge;
import com.baymax.exam.file.vo.KnowledgeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识库Feign客户端
 */
@FeignClient(value = "exam-file", contextId = "knowledge")
public interface KnowledgeClient {
    
    /**
     * 获取知识库文件详情
     *
     * @param id 文件ID
     * @return 知识库文件VO
     */
    @GetMapping("/knowledge/{id}")
    Result<KnowledgeVO> getKnowledgeInfo(@PathVariable("id") Integer id);
    
    /**
     * 搜索公开的知识库文件
     *
     * @param keyword 搜索关键词
     * @param type 文件类型
     * @param tag 标签
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 知识库文件VO分页
     */
    @GetMapping("/knowledge/search")
    Result<PageResult<KnowledgeVO>> searchPublicKnowledge(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize);
} 