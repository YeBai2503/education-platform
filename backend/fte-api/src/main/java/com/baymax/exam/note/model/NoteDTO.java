package com.baymax.exam.note.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
public class NoteDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer userId;
    
    private Integer courseId;
    
    @NotBlank(message = "笔记内容不能为空")
    private String context;
} 