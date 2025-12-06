package com.baymax.exam.experiment.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 实验提交实体类
 */
@Getter
@Setter
@TableName("ep_submit")
@Schema(name = "ExperimentSubmit", description = "实验提交信息")
public class ExperimentSubmit extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 提交ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    private Integer studentId;
    
    /**
     * 实验ID
     */
    @Schema(description = "实验ID")
    private Integer experimentId;
    
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
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 文件数量
     */
    @Schema(description = "文件数量")
    @TableField("num_file")
    private Integer numFile;
    
    /**
     * 附件1URL
     */
    @TableField("file1_URL")
    @Schema(description = "附件1URL")
    private String file1URL;
    
    /**
     * 附件2URL
     */
    @TableField("file2_URL")
    @Schema(description = "附件2URL")
    private String file2URL;
    
    /**
     * 附件3URL
     */
    @TableField("file3_URL")
    @Schema(description = "附件3URL")
    private String file3URL;
    
    /**
     * 附件4URL
     */
    @TableField("file4_URL")
    @Schema(description = "附件4URL")
    private String file4URL;
    
    /**
     * 附件5URL
     */
    @TableField("file5_URL")
    @Schema(description = "附件5URL")
    private String file5URL;
} 