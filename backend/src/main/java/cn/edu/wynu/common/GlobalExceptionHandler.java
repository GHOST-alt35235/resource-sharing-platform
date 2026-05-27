package cn.edu.wynu.common;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public AjaxResult handleNotLogin(NotLoginException e) {
        return AjaxResult.error(HttpStatus.UNAUTHORIZED.value(), "未登录");
    }

    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e) {
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e) {
        e.printStackTrace();
        return AjaxResult.error("服务器内部错误");
    }
}