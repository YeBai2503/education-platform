package com.baymax.exam.homework.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baymax.exam.center.enums.QuestionResultTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 作业作答结果
 * </p>
 *
 * @author baymax
 * @since 2023-07-05
 */
@Getter
@Setter
@TableName("eh_homework_answer_result")
@Schema(name = "HomeworkAnswerResult", description = "作业作答结果")
public class HomeworkAnswerResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "学生id")
    private Integer userId;

    @Schema(description = "作业信息id")
    private Integer homeworkInfoId;

    @Schema(description = "题目id")
    private Integer questionId;

    @Schema(description = "选项id")
    private Integer optionId;

    @Schema(description = "答案：主观题使用")
    private String answer;

    @Schema(description = "结果类型：对、错、半错")
    private QuestionResultTypeEnum resultType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 