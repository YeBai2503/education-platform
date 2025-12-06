package com.baymax.exam.file.vo;

import com.baymax.exam.file.model.Knowledge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 知识库文件VO
 */
@Data
@Schema(name = "KnowledgeVO", description = "知识库文件VO")
public class KnowledgeVO extends Knowledge {
    // 不再需要上传者信息，直接使用userId
} 