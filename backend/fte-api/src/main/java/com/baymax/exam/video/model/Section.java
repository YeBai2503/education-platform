package com.baymax.exam.video.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小节实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ev_section")
@Schema(name = "Section", description = "小节信息")
public class Section extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "章节ID")
    private Integer chapterId;

    @Schema(description = "小节标题")
    private String title;

    @Schema(description = "小节描述")
    private String description;

    @Schema(description = "排序号")
    @TableField("`rank`")
    private Integer rank;

    @Schema(description = "视频URL")
    private String videoUrl;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "视频时长(秒)")
    private Integer duration;
} 