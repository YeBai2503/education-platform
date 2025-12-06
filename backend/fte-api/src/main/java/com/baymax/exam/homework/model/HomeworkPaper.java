package com.baymax.exam.homework.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 作业试卷信息表
 * </p>
 *
 * @author baymax
 * @since 2023-05-12
 */
@Getter
@Setter
@TableName("eh_homework_paper")
@Schema(name = "HomeworkPaper", description = "作业试卷信息表")
public class HomeworkPaper extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "标题不能为空")
    @Length(min = 2,max = 255,message = "作业标题应在2~100个字符")
    @Schema(description = "作业标题")
    private String title;

    @Length(min = 2,max = 255,message = "作业介绍应在2~100个字符")
    @Schema(description = "作业介绍")
    private String introduce;

    @NotNull(message = "课程id不能为空")
    @Schema(description = "课程id")
    private Integer courseId;

    @Schema(description = "教师id")
    private Integer teacherId;
} 