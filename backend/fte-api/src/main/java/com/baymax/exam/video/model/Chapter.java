package com.baymax.exam.video.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 章节实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ev_chapter")
@Schema(name = "Chapter", description = "章节信息")
public class Chapter extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "课程ID")
    private Integer courseId;

    @Schema(description = "章节标题")
    private String title;

    @Schema(description = "章节描述")
    private String description;
} 