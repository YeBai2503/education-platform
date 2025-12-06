package com.baymax.exam.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程评分
 */
@Data
@TableName("ec_course_evaluation")
public class CourseScore extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @Schema(description = "学生ID")
    private Integer studentId;
    
    @Schema(description = "课程ID")
    private Integer courseId;
    
    @Schema(description = "评价详情")
    private String detail;

    @Schema(description = "视频评分(五分制)")
    private Integer videoScore;
    
    @Schema(description = "考试评分(五分制)")
    private Integer examScore;
    
    @Schema(description = "实验评分(五分制)")
    private Integer experimentScore;
    
    @Schema(description = "项目评分(五分制)")
    private Integer projectScore;
    
    @Schema(description = "作业评分(五分制)")
    private Integer homeworkScore;
    
    @Schema(description = "总评分(五分制，保留三位小数)")
    private Float sumScore;
} 