package com.baymax.exam.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 小节表单VO类，用于创建和更新小节
 */
@Data
@Schema(name = "SectionFormVO", description = "小节表单数据(创建/更新)")
public class SectionFormVO {

    @Schema(description = "小节ID(更新时必填)")
    private Integer id;

    @Schema(description = "章节ID(创建时必填)")
    private Integer chapterId;

    @Schema(description = "小节标题")
    private String title;

    @Schema(description = "小节描述")
    private String description;

    @Schema(description = "排序号")
    private Integer rank;
} 