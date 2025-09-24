package ssu.eatssu.global.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ControllerLogAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}

    @Around("restController()")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        String argsJson = Arrays.stream(joinPoint.getArgs())
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return String.valueOf(arg);
                    }
                })
                .collect(Collectors.joining(", "));

        log.info("REQUEST {} {} args=[{}]", method, uri, argsJson);

        try {
            Object result = joinPoint.proceed();
            long time = System.currentTimeMillis() - start;

            String resultJson;
            try {
                resultJson = objectMapper.writeValueAsString(result);
            } catch (Exception e) {
                resultJson = String.valueOf(result);
            }

            resultJson = truncate(resultJson, 200);

            log.info("RESPONSE {} {} ({} ms) result={}", method, uri, time, resultJson);
            return result;
        } catch (Throwable e) {
            long time = System.currentTimeMillis() - start;
            log.error("EXCEPTION {} {} ({} ms) cause={}", method, uri, time, e.getMessage(), e);
            throw e;
        }
    }

    private String truncate(String input, int maxLength) {
        if (input == null) return null;
        return input.length() <= maxLength
                ? input
                : input.substring(0, maxLength) + "...(truncated)";
    }
}

