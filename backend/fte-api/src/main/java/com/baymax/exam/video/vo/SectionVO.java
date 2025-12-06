package com.baymax.exam.video.vo;

import com.baymax.exam.video.model.Section;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 小节VO类，包含学习进度
 */
@Data
@Schema(name = "SectionVO", description = "小节信息(包含学习进度)")
public class SectionVO extends Section {

    @Schema(description = "学习进度(0-100)")
    private Integer progress;
    
    @Schema(description = "是否已完成")
    private Boolean completed;
} 