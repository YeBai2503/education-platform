package com.baymax.exam.experiment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实验VO类，用于前端展示和接收数据
 */
@Data
@Schema(description = "实验信息VO")
public class ExperimentVO {
    
    /**
     * 实验ID
     */
    @Schema(description = "实验ID")
    private Integer id;
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Integer courseId;
    
    /**
     * 实验标题
     */
    @NotBlank(message = "实验标题不能为空")
    @Size(max = 255, message = "实验标题长度不能超过255个字符")
    @Schema(description = "实验标题")
    private String title;
    
    /**
     * 实验详情
     */
    @Schema(description = "实验详情")
    private String detail;
    
    /**
     * 截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "截止日期")
    private LocalDateTime ddl;
    
    /**
     * 类型：0-实验，1-项目实训
     */
    @Schema(description = "类型：0-实验，1-项目实训")
    private Integer type;
    
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
    
    /**
     * 已提交学生数量（仅在查询时返回）
     */
    @Schema(description = "已提交学生数量")
    private Integer submittedCount;
} 