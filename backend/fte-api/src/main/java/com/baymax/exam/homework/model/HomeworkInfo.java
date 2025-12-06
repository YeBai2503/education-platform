package com.baymax.exam.homework.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.baymax.exam.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 作业发布信息
 * </p>
 *
 * @author baymax
 * @since 2023-05-12
 */
@Getter
@Setter
@TableName("eh_homework_info")
@Schema(name = "HomeworkInfo", description = "作业发布信息")
public class HomeworkInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "作业标题不能为空")
    @Schema(description = "作业标题")
    private String title;

    @NotNull(message = "作业id不能为空")
    @Schema(description = "作业id")
    private Integer homeworkId;

    @Schema(description = "老师id")
    private Integer teacherId;

    @NotNull(message = "课程id不能为空")
    @Schema(description = "课程id")
    private Integer courseId;

    @Schema(description = "题目乱序")
    private Boolean questionDisorder;

    @Schema(description = "选项乱序")
    private Boolean optionDisorder;

    @Schema(description = "结束可见")
    private Boolean endVisible;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Future(message = "截止时间不合法")
    @NotNull(message = "截止时间不能为空")
    @Schema(description = "截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    @Schema(description = "作业说明")
    private String description;
    
    @Schema(description = "是否允许补交")
    private Boolean allowLateSubmit;
    
    @Schema(description = "补交截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lateEndTime;
    
    @Schema(description = "补交扣分比例")
    private Float lateDeduction;
} 