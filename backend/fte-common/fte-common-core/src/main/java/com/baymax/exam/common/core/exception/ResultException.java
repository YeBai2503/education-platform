package com.baymax.exam.common.core.exception;


public class ResultException extends Exception{
    public ResultException(Object date){
        super("微服务调用异常："+date.toString());
    }
}
