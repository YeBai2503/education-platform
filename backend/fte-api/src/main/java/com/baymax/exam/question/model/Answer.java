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
 * @description：问题回答实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_answer")
public class Answer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 回答者用户ID
     */
    private Integer userId;
    
    /**
     * 问题ID
     */
    @NotNull(message = "问题ID不能为空")
    private Integer questionId;
    
    /**
     * 回答内容
     */
    @NotBlank(message = "回答内容不能为空")
    private String content;
    
    /**
     * 是否被采纳：0-未采纳，1-已采纳
     */
    private Integer isAccepted;
    
    /**
     * 点赞次数
     */
    private Integer likes;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 