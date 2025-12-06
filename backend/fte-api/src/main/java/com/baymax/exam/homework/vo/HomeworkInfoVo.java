package com.baymax.exam.homework.vo;

import com.baymax.exam.homework.model.HomeworkInfo;
import com.baymax.exam.homework.model.HomeworkPaper;
import com.baymax.exam.user.model.Classes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author ：Baymax
 * @date ：Created in 2023-05-12
 * @description：作业发布信息视图对象
 * @modified By：
 * @version:
 */
@Data
@Schema(name = "HomeworkInfoVo", description = "作业发布信息")
public class HomeworkInfoVo {
    @Valid
    @NotNull(message = "作业信息不能为空")
    private HomeworkInfo homeworkInfo;
    @NotEmpty(message = "班级列表不能为空")
    private Set<Integer> classIds;
    private HomeworkPaper paper;
    private List<Classes> classList;
} 