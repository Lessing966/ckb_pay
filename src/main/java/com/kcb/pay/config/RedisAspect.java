package com.kcb.pay.config;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * TODO redis切面处理类
 *
 */
@Aspect
@Configuration
public class RedisAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 是否启用redis 开启-true 关闭-false(默认)
     * 对应配置文件spring.redis.open
     */
    @Value("${spring.redis.open: false}")
    private boolean open;

    @Around("execution(* com.kcb.pay.common.RedisUtils.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        if(open){
            try{
                result = point.proceed();
            }catch (Exception e){
                logger.error("redis error", e);
                throw new Exception("Redis服务异常");
            }
        }
        return result;
    }
}
