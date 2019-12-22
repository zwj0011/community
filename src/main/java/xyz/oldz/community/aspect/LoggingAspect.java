package xyz.oldz.community.aspect;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Pointcut("execution(public * xyz.oldz.community.controller.*.*(..))")
  public void pointCut() {
  }

  @Around("pointCut()")
  public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    request.getParameterMap();
    log.info("【正在访问】{}:{}{}", request.getMethod(), request.getRequestURL(),
        request.getQueryString() != null ? ("?" + request.getQueryString()) : "");
    long startTime = System.currentTimeMillis();
    Object result = pjp.proceed();
    log.info("【结束访问】{}:{}{} 耗时：{}ms", request.getMethod(), request.getRequestURL(),
        request.getQueryString() != null ? ("?" + request.getQueryString()) : "",
        System.currentTimeMillis() - startTime);
    return result;
  }

}
