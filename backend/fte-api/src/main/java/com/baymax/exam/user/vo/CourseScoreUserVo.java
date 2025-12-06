package com.baymax.exam.user.vo;

import com.baymax.exam.user.model.CourseScore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 包含用户信息的课程评分VO
 */
@Data
public class CourseScoreUserVo {
    
    @Schema(description = "评分信息")
    private CourseScore courseScore;
    
    @Schema(description = "学生ID")
    private Integer studentId;
    
    @Schema(description = "学生昵称")
    private String nickname;
    
    @Schema(description = "学生头像")
    private String picture;
} 