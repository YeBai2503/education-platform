package com.baymax.exam.file.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 知识库文件信息
 */
@Getter
@Setter
@TableName("ek_knowledge")
@Schema(name = "Knowledge", description = "知识库文件信息")
public class Knowledge extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "用户ID")
    private Integer userId;

    @NotBlank(message = "文件名称不能为空")
    @Schema(description = "文件名称")
    private String name;

    @Pattern(regexp = "0|1", message = "公开状态不合法")
    @Schema(description = "是否公开:0：不公开,1：公开")
    private String isPublic;

    @Schema(description = "文件URL")
    private String url;

    @Schema(description = "文件标签，多个标签用逗号分隔")
    private String tag;

    @Schema(description = "文件类型")
    private String type;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
} 