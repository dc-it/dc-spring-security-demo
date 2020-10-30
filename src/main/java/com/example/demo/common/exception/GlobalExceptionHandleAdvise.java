package com.example.demo.common.exception;

import com.example.demo.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author duchao
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandleAdvise {

    @ExceptionHandler({ValidationException.class,
            MethodArgumentNotValidException.class,
            HttpRequestMethodNotSupportedException.class})
    public Result handleRequestException(Exception e) {
        String errorMsg = null;
        if (e instanceof MethodArgumentNotValidException) {
            errorMsg = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().stream().map(item -> item.getDefaultMessage()).collect(Collectors.joining(","));
        } else if (e instanceof ValidationException) {
            errorMsg = e.getMessage();
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            errorMsg = "接口请求方式错误";
        }
        log.error("接口入参异常", errorMsg);
        return Result.failure(errorMsg);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("接口404：{}", request.getRequestURI());
        return Result.failure("接口404");
    }

    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.failure(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("无权访问：{}", request.getRequestURI());
        return Result.failure("无权访问");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败：{}", e.getMessage());
        return Result.failure(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage());
        return Result.failure(MessageFormat.format("系统异常：{0}", e.getMessage()));
    }

}
