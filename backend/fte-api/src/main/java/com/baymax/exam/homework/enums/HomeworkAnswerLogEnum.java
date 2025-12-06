package com.baymax.exam.homework.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * @author ：Baymax
 * @date ：Created in 2023-07-05
 * @description：作业作答日志枚举
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HomeworkAnswerLogEnum {
    //大于10：正常行为
    START(11,"开始作答"),
    SUBMIT(12,"提交作业"),
    ROBOT_REVIEW(21,"机器批阅"),
    TEACHER_REVIEW(23,"教师批阅"),
    ///大于20：答题行为
    PROGRESS(41,"作答进度");

    @Getter
    private String action;
    @Getter
    @EnumValue
    private Integer value;
    HomeworkAnswerLogEnum(Integer value, String action){
        this.action=action;
        this.value=value;
    }
} 