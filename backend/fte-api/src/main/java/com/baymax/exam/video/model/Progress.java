package com.baymax.exam.video.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学习进度实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ev_progress")
@Schema(name = "Progress", description = "学习进度信息")
public class Progress extends BaseEntity {

    @Schema(description = "学生ID")
    private Integer studentId;

    @Schema(description = "小节ID")
    private Integer sectionId;

    @Schema(description = "学习进度(百分制)")
    private Integer progress;
} 