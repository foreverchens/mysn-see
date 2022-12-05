package com.ychen.see.common.Advice;

import com.ychen.see.common.CallResult;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;


/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
@Slf4j
@RestControllerAdvice
public class ExceptAdvice {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public CallResult<String> handleExcept(Exception ex) {
        log.error(ex.getMessage(), ex);
        return CallResult.failure(ex.getMessage());
    }
}
