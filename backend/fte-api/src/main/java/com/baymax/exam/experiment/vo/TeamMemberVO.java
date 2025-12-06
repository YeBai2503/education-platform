package com.baymax.exam.experiment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 队伍成员VO类，用于前端展示
 */
@Data
@Schema(description = "队伍成员信息VO")
public class TeamMemberVO {
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
    
    /**
     * 用户姓名
     */
    @Schema(description = "用户姓名")
    private String userName;
    
    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userPicture;
    
    /**
     * 是否为队长
     */
    @Schema(description = "是否为队长")
    private Boolean isHeader;
    
    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "加入时间")
    private LocalDateTime joinTime;
} 