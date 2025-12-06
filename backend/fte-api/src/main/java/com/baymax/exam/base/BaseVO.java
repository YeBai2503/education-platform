package com.baymax.exam.base;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
public class BaseVO implements Serializable {

    //序列化的版本控制标识符。
    private static final long serialVersionUID = 1L;
}
