package edu.sharif.ce.apyugioh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;

@Aspect
public class AspectLogging {
    private static Logger logger = LogManager.getLogger(AspectLogging.class);

    @AfterThrowing(value = "execution(* edu.sharif.ce.apyugioh.controller..*.*(..))", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception occurred in {}.{}()\ncause: {}\nexception: {}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL", e.toString());
    }

    @Around("execution(* edu.sharif.ce.apyugioh.controller..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (joinPoint.getArgs().length > 0)
            logger.info("Enter: {}.{}() with arguments: {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        else
            logger.info("Enter: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
        try {
            Object result = joinPoint.proceed();
            logger.info("Exit {}.{}() with result: {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
