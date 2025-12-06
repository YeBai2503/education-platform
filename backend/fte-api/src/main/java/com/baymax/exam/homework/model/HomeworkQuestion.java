package com.baymax.exam.homework.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 作业试题关联表
 * </p>
 *
 * @author baymax
 * @since 2023-05-12
 */
@Getter
@Setter
@TableName("eh_homework_question")
@Schema(name = "HomeworkQuestion", description = "作业试题关联表")
public class HomeworkQuestion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "题目id不能为空")
    @Schema(description = "题目id")
    private Integer questionId;

    @NotNull(message = "作业id不能为空")
    @Schema(description = "作业id")
    private Integer homeworkId;
} 