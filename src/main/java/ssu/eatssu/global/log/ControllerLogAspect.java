package ssu.eatssu.global.log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}

    @Around("restController()")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        Object[] args = joinPoint.getArgs();

        log.info("REQUEST {} {} args={}", method, uri, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed(); 
            long time = System.currentTimeMillis() - start;
            log.info("RESPONSE {} {} ({} ms) result={}", method, uri, time, result);
            return result;
        } catch (Throwable e) {
            long time = System.currentTimeMillis() - start;
            log.error("EXCEPTION {} {} ({} ms) cause={}", method, uri, time, e.getMessage(), e);
            throw e;
        }
    }
}
