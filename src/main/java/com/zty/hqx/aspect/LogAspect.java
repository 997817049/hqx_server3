package com.zty.hqx.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志
 *
 * @author funyoo
 * @creatDate 2019/12/02 15:00
 */
@Aspect
@Component
public class LogAspect {
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    private Logger logger = LogManager.getLogger(LogAspect.class);

    //统一切点,对com.funyoo.hqxApp.controller及其子包中所有的类的所有方法切面
    @Pointcut("execution(public * com.zty.hqx.controller..*.*(..))")
    public void Pointcut() {
    }

    //前置通知
    @Before("Pointcut()")
    public void beforeMethod(JoinPoint joinPoint) throws ClassNotFoundException, NoSuchMethodException {
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getRemoteAddr();
            String url = request.getRequestURI();//去除localhost的路径
            String type = request.getMethod();
            //详细方法名 = 类名 + 方法名
            String DeclaringMethodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            // 参数值
            Object[] args = joinPoint.getArgs();
            String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames(); // 参数名
            StringBuilder para = new StringBuilder();
            for(int i = 0; i < argNames.length; i++) {
                para.append(argNames[i]).append("=").append(args[i]).append(" ");
            }
            logger.info("[" + ip + "] [" + url + "] [" + type + "] [" + DeclaringMethodName + "] [" + para.toString() + "]");
        }
    }

    //@AfterRunning: 返回通知 rsult为返回内容
    @AfterReturning(value="Pointcut()",returning="result")
    public void afterReturningMethod(JoinPoint joinPoint, Object result){
        logger.info("[" + result + "] [" + (System.currentTimeMillis() - startTime.get()) + "ms]");
    }

    //@AfterThrowing: 异常通知
    @AfterThrowing(value="Pointcut()",throwing="e")
    public void afterReturningMethod(JoinPoint joinPoint, Exception e){
        logger.error(e.getMessage());
        logger.error(e.getLocalizedMessage());
    }

}
