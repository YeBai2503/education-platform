package com.baymax.exam.question.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ：Baymax
 * @date ：Created in 2023/05/15
 * @description：问题状态枚举
 */
@Getter
@AllArgsConstructor
public enum QuestionStatusEnum {
    
    UNSOLVED(0, "未解决"),
    SOLVED(1, "已解决");
    
    private final Integer code;
    private final String desc;
} 