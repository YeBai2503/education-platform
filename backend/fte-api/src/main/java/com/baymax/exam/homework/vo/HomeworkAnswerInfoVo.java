package com.baymax.exam.homework.vo;

import com.baymax.exam.homework.model.HomeworkAnswerResult;
import com.baymax.exam.homework.model.HomeworkScoreRecord;
import lombok.Data;

import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2023-07-05
 * @description：作业作答情况
 */
@Data
public class HomeworkAnswerInfoVo {
    private HomeworkScoreRecord scoreRecord;
    private List<HomeworkAnswerResult> answerResult;
} 