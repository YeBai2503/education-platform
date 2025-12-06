package com.baymax.exam.question.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：问题数据传输对象
 */
@Data
public class QuestionDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer userId;
    
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    @NotBlank(message = "问题标题不能为空")
    private String title;
    
    @NotBlank(message = "问题内容不能为空")
    private String content;
} 