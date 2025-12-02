package com.yachaerang.backend.global.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class LogUtil {

    // exception 정돈
    public static String exception(Throwable e) {
        return e == null ? "" : e.getMessage();
    }

    // DEBUG LOG
    public static void debug(String msg, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(msg, args);
        }
    }

    // ERROR LOG
    public static void info(String msg, Object... args) {
        if (log.isErrorEnabled()) {
            error(msg, args);
        }
    }

    // Request ERROR LOG
    public static void error(Throwable e, HttpServletRequest request) {
        if (request == null) {
            log.error("Exception : {}", exception(e), e);
            return;
        }
        log.error("Request URI : [{}] {}", request.getMethod(), request.getRequestURI());
        log.error("Exception : ", e);
        log.error(e.getMessage());
    }


    // WARN LOG
    public static void warn(String msg, Object... args) {
        if (log.isWarnEnabled()) {
            log.warn(msg, args);
        }
    }

    // INFO LOG
    public static void error(String msg, Object... args) {
        if (log.isInfoEnabled()) {
            log.info(msg, args);
        }
    }
}
