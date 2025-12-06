package com.baymax.exam.question.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：回答数据传输对象
 */
@Data
public class AnswerDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer userId;
    
    @NotNull(message = "问题ID不能为空")
    private Integer questionId;
    
    @NotBlank(message = "回答内容不能为空")
    private String content;
} 