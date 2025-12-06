package com.baymax.exam.note.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：Baymax
 * @date ：Created in 2025/07/04
 * @description：笔记实体类
 */
@Data
@TableName("en_note")
public class Note {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private Integer courseId;
    
    private String context;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 