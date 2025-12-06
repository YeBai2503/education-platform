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
 * 项目实训队伍实体类
 */
@Getter
@Setter
@TableName("ep_team")
@Schema(name = "Team", description = "项目实训队伍信息")
public class Team extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 队伍ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 项目实训ID
     */
    @Schema(description = "项目实训ID")
    private Integer experimentId;
    
    /**
     * 队伍名称
     */
    @Schema(description = "队伍名称")
    private String name;
    
    /**
     * 队长ID
     */
    @Schema(description = "队长ID")
    private Integer headerId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 