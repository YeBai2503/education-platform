package com.baymax.exam.note.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：Baymax
 * @date ：Created in 2025/07/04
 * @description：笔记展示对象
 */
@Data
public class NoteVO {
    
    private Integer id;
    
    private Integer userId;
    
    private Integer courseId;
    
    private String context;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 