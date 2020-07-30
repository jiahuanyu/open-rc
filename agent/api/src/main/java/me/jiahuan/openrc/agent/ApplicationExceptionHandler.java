package me.jiahuan.openrc.agent;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ApplicationExceptionHandler {

    /**
     * 捕获所有异常
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public String handleAllException(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Throwable error) {
        return "";
    }
}

