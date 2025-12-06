package com.baymax.exam.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baymax.exam.file.model.Knowledge;
import com.baymax.exam.file.vo.KnowledgeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 知识库Mapper接口
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
    
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
     * @param page 分页对象
     * @param userId 用户ID
     * @param type 文件类型，可为null
     * @param tag 标签，可为null
     * @return 知识库文件VO分页
     */
    IPage<KnowledgeVO> getUserKnowledgeList(IPage<KnowledgeVO> page, 
                                          @Param("userId") Integer userId, 
                                          @Param("type") String type, 
                                          @Param("tag") String tag);
    
    /**
     * 搜索公开的知识库文件
     *
     * @param page 分页对象
     * @param keyword 搜索关键词，可为null
     * @param type 文件类型，可为null
     * @param tag 标签，可为null
     * @return 知识库文件VO分页
     */
    IPage<KnowledgeVO> searchPublicKnowledge(IPage<KnowledgeVO> page, 
                                           @Param("keyword") String keyword, 
                                           @Param("type") String type, 
                                           @Param("tag") String tag);
} 