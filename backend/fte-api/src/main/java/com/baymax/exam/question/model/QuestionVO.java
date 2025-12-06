package com.baymax.exam.question.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：问题展示对象
 */
@Data
public class QuestionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer userId;
    
    private String username;
    
    private String userAvatar;
    
    private Integer courseId;
    
    private String courseName;
    
    private String title;
    
    private String content;
    
    private Integer status;
    
    private Integer views;
    
    private Integer likes;
    
    private Integer favorites;
    
    private Integer answerCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 