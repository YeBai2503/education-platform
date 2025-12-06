package com.baymax.exam.question.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：评论展示对象
 */
@Data
public class CommentVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer userId;
    
    private String username;
    
    private String userAvatar;
    
    private Integer type;
    
    private Integer relatedId;
    
    private Integer parentId;
    
    private String content;
    
    private List<CommentVO> children;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
} 