package com.baymax.exam.web.exception;

import com.baymax.exam.common.core.exception.ResultException;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//通用异常处理

@RestControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(value = FileSizeLimitExceededException.class)
    @ResponseBody
    public Result handleFileSizeLimitExceededException(FileSizeLimitExceededException e) {
        return Result.failed(ResultCode.PARAM_ERROR,"文件上传大小超出限制");
    }
    
    @ExceptionHandler(value = ResultException.class)
    @ResponseBody
    public Result handleResultException(ResultException e) {
        return Result.failed(ResultCode.CALL_THIRD_PARTY_SERVICE_ERROR, e.getMessage());
    }
}
