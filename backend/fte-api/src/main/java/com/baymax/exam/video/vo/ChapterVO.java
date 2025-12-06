package com.baymax.exam.video.vo;

import com.baymax.exam.video.model.Chapter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 章节VO类，包含小节列表
 */
@Data
@Schema(name = "ChapterVO", description = "章节信息(包含小节列表)")
public class ChapterVO extends Chapter {

    @Schema(description = "小节列表")
    private List<SectionVO> sections;
} 