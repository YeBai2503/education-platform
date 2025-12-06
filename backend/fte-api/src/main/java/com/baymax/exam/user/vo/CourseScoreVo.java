package com.baymax.exam.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程评分VO
 */
@Data
public class CourseScoreVo {
    
    @Schema(description = "课程ID")
    private Integer courseId;
    
    @Schema(description = "课程名称")
    private String courseName;
    
    @Schema(description = "视频评分(五分制)")
    private Double videoScore;
    
    @Schema(description = "考试评分(五分制)")
    private Double examScore;
    
    @Schema(description = "实验评分(五分制)")
    private Double experimentScore;
    
    @Schema(description = "项目评分(五分制)")
    private Double projectScore;
    
    @Schema(description = "作业评分(五分制)")
    private Double homeworkScore;
    
    @Schema(description = "总评分(五分制)")
    private Double sumScore;
    
    @Schema(description = "评分人数")
    private Integer scoreCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最近评价时间")
    private LocalDateTime latestScoreTime;
} 