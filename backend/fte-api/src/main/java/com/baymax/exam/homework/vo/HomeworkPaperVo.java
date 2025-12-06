package com.baymax.exam.homework.vo;

import com.baymax.exam.homework.model.HomeworkPaper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author ：Baymax
 * @date ：Created in 2023-05-12
 * @description：作业试卷视图对象
 * @modified By：
 * @version:
 */
@Data
@Schema(name = "HomeworkPaperVo", description = "作业题目具体信息")
public class HomeworkPaperVo {
    @Valid
    @NotNull(message = "作业信息不能为空")
    HomeworkPaper homeworkPaper;
    Set<Integer> questions;
} 