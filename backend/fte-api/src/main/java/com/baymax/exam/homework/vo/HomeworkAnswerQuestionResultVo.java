package com.baymax.exam.homework.vo;

import com.baymax.exam.center.model.Question;
import com.baymax.exam.center.vo.QuestionInfoVo;
import com.baymax.exam.homework.model.HomeworkAnswerResult;
import com.baymax.exam.homework.model.HomeworkScoreRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2023-07-05
 * @description：作业题目作答结果
 */
@Data
public class HomeworkAnswerQuestionResultVo {
    @Schema(description = "题目信息")
    private QuestionInfoVo questionInfo;
    @Schema(description = "作答信息")
    private List<HomeworkAnswerResult> answerResult;
    @Schema(description = "批阅结果")
    private HomeworkScoreRecord scoreRecord;
} 