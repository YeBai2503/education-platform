package com.baymax.exam.homework.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baymax.exam.base.BaseEntity;
import com.baymax.exam.homework.enums.HomeworkAnswerLogEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;

/**
 * <p>
 * 作业作答日志
 * </p>
 *
 * @author baymax
 * @since 2023-07-05
 */
@Getter
@Setter
@TableName("eh_homework_answer_log")
@Schema(name = "HomeworkAnswerLog", description = "作业作答日志")
public class HomeworkAnswerLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "作业信息id")
    private Integer homeworkInfoId;

    @Schema(description = "学生id")
    private Integer studentId;
    
    @Schema(description = "班级id")
    private Integer classId;
    
    @Schema(description = "作业id")
    private Integer homeworkId;

    @Null(message = "状态不能为空")
    @Schema(description = "状态：11:开始、12：提交")
    private HomeworkAnswerLogEnum status;

    @Schema(description = "状态信息")
    private String info;
} 