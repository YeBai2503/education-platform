package com.baymax.exam.experiment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实验提交VO类，用于前端展示和接收数据
 */
@Data
@Schema(description = "实验提交信息VO")
public class ExperimentSubmitVO {
    
    /**
     * 提交ID
     */
    @Schema(description = "提交ID")
    private Integer id;
    
    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    private Integer studentId;
    
    /**
     * 学生姓名（仅在查询时返回）
     */
    @Schema(description = "学生姓名")
    private String studentName;
    
    /**
     * 实验ID
     */
    @NotNull(message = "实验ID不能为空")
    @Schema(description = "实验ID")
    private Integer experimentId;
    
    /**
     * 实验标题（仅在查询时返回）
     */
    @Schema(description = "实验标题")
    private String experimentTitle;
    
    /**
     * 提交的详情备注
     */
    @Schema(description = "提交的详情备注")
    private String detail;
    
    /**
     * 评分
     */
    @Schema(description = "评分")
    private Integer score;
    
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
     * 附件URL列表，最多5个
     */
    @Schema(description = "附件URL列表，最多5个")
    private List<String> fileUrls;
} 