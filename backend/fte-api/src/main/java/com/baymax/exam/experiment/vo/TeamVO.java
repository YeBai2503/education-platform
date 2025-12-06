package com.baymax.exam.experiment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 队伍VO类，用于前端展示和接收数据
 */
@Data
@Schema(description = "队伍信息VO")
public class TeamVO {
    
    /**
     * 队伍ID
     */
    @Schema(description = "队伍ID")
    private Integer id;
    
    /**
     * 项目实训ID
     */
    @NotNull(message = "项目实训ID不能为空")
    @Schema(description = "项目实训ID")
    private Integer experimentId;
    
    /**
     * 队伍名称
     */
    @NotBlank(message = "队伍名称不能为空")
    @Schema(description = "队伍名称")
    private String name;
    
    /**
     * 队长ID
     */
    @Schema(description = "队长ID")
    private Integer headerId;
    
    /**
     * 队长姓名
     */
    @Schema(description = "队长姓名")
    private String headerName;
    
    /**
     * 队伍成员列表
     */
    @Schema(description = "队伍成员列表")
    private List<TeamMemberVO> members;
    
    /**
     * 成员数量
     */
    @Schema(description = "成员数量")
    private Integer memberCount;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    /**
     * 队伍得分（从队长的提交记录中获取）
     */
    @Schema(description = "队伍得分")
    private Integer score;
} 