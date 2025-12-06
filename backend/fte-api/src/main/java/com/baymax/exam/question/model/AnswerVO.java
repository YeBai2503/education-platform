package com.baymax.exam.question.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：回答展示对象
 */
@Data
public class AnswerVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer userId;
    
    private String username;
    
    private String userAvatar;
    
    private Integer questionId;
    
    private String content;
    
    private Integer isAccepted;
    
    private Integer likes;
    
    private Integer commentCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 