package com.baymax.exam.question.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description：评论类型枚举
 */
@Getter
@AllArgsConstructor
public enum CommentTypeEnum {
    
    /**
     * 问题评论
     */
    QUESTION_COMMENT(1, "问题评论"),
    
    /**
     * 回答评论
     */
    ANSWER_COMMENT(2, "回答评论");
    
    private final Integer code;
    private final String desc;
} 