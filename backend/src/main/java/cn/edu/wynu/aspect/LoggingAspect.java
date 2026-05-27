package cn.edu.wynu.aspect;

import cn.dev33.satoken.stp.StpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Pointcut("execution(* cn.edu.wynu.controller..*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
        } catch (Exception e) {
        }

        String timestamp = LocalDateTime.now().format(formatter);
        logger.info("[{}] User:{} -> {}.{}", timestamp, userId, className, methodName);

        Object result = null;
        try {
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            logger.info("[{}] User:{} -> {}.{} 执行耗时: {}ms", timestamp, userId, className, methodName, endTime - startTime);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[{}] User:{} -> {}.{} 执行失败: {} (耗时: {}ms)", timestamp, userId, className, methodName, e.getMessage(), endTime - startTime);
            throw e;
        }

        return result;
    }
}
