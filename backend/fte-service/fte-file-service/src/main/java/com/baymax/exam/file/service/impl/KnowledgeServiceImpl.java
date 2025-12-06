package com.baymax.exam.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.file.mapper.KnowledgeMapper;
import com.baymax.exam.file.model.Knowledge;
import com.baymax.exam.file.service.IKnowledgeService;
import com.baymax.exam.file.utils.KnowledgeFileUtils;
import com.baymax.exam.file.vo.KnowledgeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 知识库服务实现类
 */
@Slf4j
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {
    
    @Autowired
    private KnowledgeMapper knowledgeMapper;
    
    @Autowired
    private KnowledgeFileUtils knowledgeFileUtils;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeVO uploadKnowledge(MultipartFile file, Knowledge knowledge) throws IOException {
        try {
            // 上传文件
            String fileUrl = knowledgeFileUtils.uploadKnowledgeFile(file, knowledge.getUserId());
            
            // 设置文件URL
            knowledge.setUrl(fileUrl);
            
            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            knowledge.setCreatedAt(now);
            knowledge.setUpdatedAt(now);
            
            // 保存知识库记录
            save(knowledge);
            
            // 返回VO
            return getKnowledgeInfo(knowledge.getId());
        } catch (IOException e) {
            log.error("上传知识库文件失败", e);
            throw e;
        }
    }
    
    @Override
    public KnowledgeVO getKnowledgeInfo(Integer id) {
        return knowledgeMapper.getKnowledgeInfo(id);
    }
    
    @Override
    public IPage<KnowledgeVO> getUserKnowledgeList(Integer userId, String type, String tag, long currentPage, long pageSize) {
        Page<KnowledgeVO> page = new Page<>(currentPage, pageSize);
        return knowledgeMapper.getUserKnowledgeList(page, userId, type, tag);
    }
    
    @Override
    public IPage<KnowledgeVO> searchPublicKnowledge(String keyword, String type, String tag, long currentPage, long pageSize) {
        Page<KnowledgeVO> page = new Page<>(currentPage, pageSize);
        return knowledgeMapper.searchPublicKnowledge(page, keyword, type, tag);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteKnowledge(Integer id, Integer userId) {
        try {
            // 获取知识库记录
            Knowledge knowledge = getById(id);
            if (knowledge == null) {
                log.warn("知识库文件不存在: {}", id);
                return false;
            }
            
            // 权限检查
            if (!knowledge.getUserId().equals(userId)) {
                log.warn("无权删除知识库文件: 文件ID={}, 用户ID={}, 文件所有者={}", id, userId, knowledge.getUserId());
                return false;
            }
            
            // 删除文件
            boolean fileDeleted = knowledgeFileUtils.deleteKnowledgeFile(knowledge.getUrl());
            
            // 删除数据库记录
            boolean recordDeleted = removeById(id);
            
            if (!fileDeleted) {
                log.warn("知识库文件删除失败: {}", knowledge.getUrl());
            }
            
            return recordDeleted;
        } catch (Exception e) {
            log.error("删除知识库文件失败", e);
            throw e;
        }
    }
} 