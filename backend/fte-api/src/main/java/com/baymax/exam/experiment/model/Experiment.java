package com.baymax.exam.experiment.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 实验发布实体类
 */
@Getter
@Setter
@TableName("ep_publish")
@Schema(name = "Experiment", description = "实验发布信息")
public class Experiment extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 实验ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Integer courseId;
    
    /**
     * 实验标题
     */
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
    @Schema(description = "截止日期")
    private LocalDateTime ddl;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 类型：0-实验，1-项目实训
     */
    @Schema(description = "类型：0-实验，1-项目实训")
    private Integer type;
    
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