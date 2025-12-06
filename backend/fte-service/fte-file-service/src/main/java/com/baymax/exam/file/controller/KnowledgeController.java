package com.baymax.exam.file.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baymax.exam.common.core.result.PageResult;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.file.model.Knowledge;
import com.baymax.exam.file.service.IKnowledgeService;
import com.baymax.exam.file.vo.KnowledgeVO;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识库控制器
 */
@Slf4j
@Validated
@Tag(name = "知识库管理")
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {
    
    @Autowired
    private IKnowledgeService knowledgeService;
    
    /**
     * 安全获取用户ID，如果出现异常则返回null
     */
    private Integer safeGetUserId() {
        try {
            return UserAuthUtil.getUserId();
        } catch (Exception e) {
            log.warn("获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Operation(summary = "上传知识库文件")
    @PostMapping("/upload")
    public Result<KnowledgeVO> uploadKnowledge(@RequestPart("file") MultipartFile file,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false, defaultValue = "1") String isPublic,
                                              @RequestParam(required = false, defaultValue = "其他") String tag,
                                              @RequestParam(required = false, defaultValue = "其他") String type) {
        try {
            // 获取当前用户ID
            Integer userId = safeGetUserId();
            if (userId == null) {
                return Result.failed("未授权，请先登录");
            }
            
            // 如果没有提供文件名，使用原始文件名
            if (name == null || name.isEmpty()) {
                name = file.getOriginalFilename();
            }
            
            // 创建知识库记录
            Knowledge knowledge = new Knowledge();
            knowledge.setUserId(userId);
            knowledge.setName(name);
            knowledge.setIsPublic(isPublic);
            knowledge.setTag(tag);
            knowledge.setType(type);
            
            // 上传文件并保存记录
            KnowledgeVO result = knowledgeService.uploadKnowledge(file, knowledge);
            return Result.success("上传成功", result);
        } catch (Exception e) {
            log.error("上传知识库文件失败", e);
            return Result.failed("上传失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取知识库文件详情")
    @GetMapping("/{id}")
    public Result<KnowledgeVO> getKnowledgeInfo(@PathVariable Integer id) {
        try {
            // 获取知识库文件详情
            KnowledgeVO knowledge = knowledgeService.getKnowledgeInfo(id);
            if (knowledge == null) {
                return Result.failed("文件不存在");
            }
            
            // 检查权限
            Integer userId = safeGetUserId();
            if (!"1".equals(knowledge.getIsPublic()) && (userId == null || !userId.equals(knowledge.getUserId()))) {
                return Result.failed("无权访问此文件");
            }
            
            return Result.success(knowledge);
        } catch (Exception e) {
            log.error("获取知识库文件详情失败", e);
            return Result.failed("获取失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户的知识库文件列表")
    @GetMapping("/user")
    public Result<PageResult<KnowledgeVO>> getUserKnowledgeList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize) {
        try {
            // 获取当前用户ID
            Integer userId = safeGetUserId();
            if (userId == null) {
                return Result.failed("未授权，请先登录");
            }
            
            // 查询用户的知识库文件
            IPage<KnowledgeVO> result = knowledgeService.getUserKnowledgeList(userId, type, tag, currentPage, pageSize);
            return Result.success(PageResult.setResult(result));
        } catch (Exception e) {
            log.error("获取用户知识库文件列表失败", e);
            return Result.failed("获取失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "搜索公开的知识库文件")
    @GetMapping("/search")
    public Result<PageResult<KnowledgeVO>> searchPublicKnowledge(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize) {
        try {
            // 搜索公开的知识库文件
            IPage<KnowledgeVO> result = knowledgeService.searchPublicKnowledge(keyword, type, tag, currentPage, pageSize);
            return Result.success(PageResult.setResult(result));
        } catch (Exception e) {
            log.error("搜索公开知识库文件失败", e);
            return Result.failed("搜索失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "删除知识库文件")
    @DeleteMapping("/{id}")
    public Result deleteKnowledge(@PathVariable Integer id) {
        try {
            // 获取当前用户ID
            Integer userId = safeGetUserId();
            if (userId == null) {
                return Result.failed("未授权，请先登录");
            }
            
            // 删除知识库文件
            boolean success = knowledgeService.deleteKnowledge(id, userId);
            if (success) {
                return Result.msgSuccess("删除成功");
            } else {
                return Result.failed("删除失败，可能没有权限或文件不存在");
            }
        } catch (Exception e) {
            log.error("删除知识库文件失败", e);
            return Result.failed("删除失败: " + e.getMessage());
        }
    }
} 