package com.baymax.exam.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.file.model.Knowledge;
import com.baymax.exam.file.vo.KnowledgeVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 知识库服务接口
 */
public interface IKnowledgeService extends IService<Knowledge> {
    
    /**
     * 上传知识库文件
     *
     * @param file 文件
     * @param knowledge 知识库信息
     * @return 知识库文件VO
     * @throws IOException 文件处理异常
     */
    KnowledgeVO uploadKnowledge(MultipartFile file, Knowledge knowledge) throws IOException;
    
    /**
     * 获取知识库文件详情
     *
     * @param id 文件ID
     * @return 知识库文件VO
     */
    KnowledgeVO getKnowledgeInfo(Integer id);
    
    /**
     * 分页查询用户的知识库文件
     *
     * @param userId 用户ID
     * @param type 文件类型，可为null
     * @param tag 标签，可为null
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 知识库文件VO分页
     */
    IPage<KnowledgeVO> getUserKnowledgeList(Integer userId, String type, String tag, long currentPage, long pageSize);
    
    /**
     * 搜索公开的知识库文件
     *
     * @param keyword 搜索关键词，可为null
     * @param type 文件类型，可为null
     * @param tag 标签，可为null
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 知识库文件VO分页
     */
    IPage<KnowledgeVO> searchPublicKnowledge(String keyword, String type, String tag, long currentPage, long pageSize);
    
    /**
     * 删除知识库文件
     *
     * @param id 文件ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     */
    boolean deleteKnowledge(Integer id, Integer userId);
} 