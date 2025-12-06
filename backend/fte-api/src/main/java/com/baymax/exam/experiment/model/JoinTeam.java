package com.baymax.exam.experiment.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 队伍成员实体类
 */
@Getter
@Setter
@TableName("ep_join_team")
@Schema(name = "JoinTeam", description = "队伍成员信息")
public class JoinTeam extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
    
    /**
     * 队伍ID
     */
    @Schema(description = "队伍ID")
    private Integer teamId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 