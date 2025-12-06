package com.baymax.exam.question.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_comment")
public class Comment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 评论者用户ID
     */
    private Integer userId;
    
    /**
     * 评论类型：1-问题评论，2-回答评论
     */
    @NotNull(message = "评论类型不能为空")
    private Integer type;
    
    /**
     * 关联ID，问题ID或回答ID
     */
    @NotNull(message = "关联ID不能为空")
    private Integer relatedId;
    
    /**
     * 父评论ID，0表示一级评论
     */
    private Integer parentId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 