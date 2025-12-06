package com.baymax.exam.video.vo;

import lombok.Data;

/**
 * 小节表单数据VO
 */
@Data
public class SectionFormVO {
    /**
     * 小节ID（更新时使用）
     */
    private Integer id;
    
    /**
     * 章节ID
     */
    private Integer chapterId;
    
    /**
     * 小节标题
     */
    private String title;
    
    /**
     * 小节描述
     */
    private String description;
    
    /**
     * 排序
     */
    private Integer rank;
} 