package com.baymax.exam.question.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：评论数据传输对象
 */
@Data
public class CommentDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer userId;
    
    @NotNull(message = "评论类型不能为空")
    private Integer type;
    
    @NotNull(message = "关联ID不能为空")
    private Integer relatedId;
    
    private Integer parentId;
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
} 