package com.baymax.exam.homework.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 作业班级关联表
 * </p>
 *
 * @author baymax
 * @since 2023-05-12
 */
@Getter
@Setter
@TableName("eh_homework_class")
@Schema(name = "HomeworkClass", description = "作业班级关联表")
public class HomeworkClass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "班级id")
    private Integer classId;

    @Schema(description = "作业信息id")
    private Integer homeworkInfoId;
} 