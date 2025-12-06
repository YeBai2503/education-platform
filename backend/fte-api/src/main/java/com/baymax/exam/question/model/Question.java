package com.baymax.exam.question.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：问题实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_question")
public class Question implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 发布问题的用户ID
     */
    private Integer userId;
    
    /**
     * 问题所属课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    /**
     * 问题标题
     */
    @NotBlank(message = "问题标题不能为空")
    private String title;
    
    /**
     * 问题内容
     */
    @NotBlank(message = "问题内容不能为空")
    private String content;
    
    /**
     * 问题状态：0-未解决，1-已解决
     */
    private Integer status;
    
    /**
     * 浏览次数
     */
    private Integer views;
    
    /**
     * 点赞次数
     */
    private Integer likes;
    
    /**
     * 收藏次数
     */
    private Integer favorites;
    
    /**
     * 回答数量
     */
    private Integer answerCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 