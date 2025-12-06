package com.baymax.exam.note.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ：Baymax
 * @date ：Created in 2025/07/04
 * @description：笔记数据传输对象
 */
@Data
public class NoteDTO {
    
    private Integer id;
    
    private Integer userId;
    
    private Integer courseId;
    
    private String context;
} 