package ssu.eatssu.global.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ssu.eatssu.domain.auth.security.CustomUserDetails;

import java.util.stream.Collectors;
import java.util.stream.IntStream;


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

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // 요청자
        String userIdLog = IntStream.range(0, args.length)
                .filter(i -> args[i] instanceof CustomUserDetails)
                .mapToObj(i -> {
                    CustomUserDetails user = (CustomUserDetails) args[i];
                    return "userId=" + user.getId();
                })
                .findFirst()
                .orElse("userId=anonymous");

        // 나머지 요청 인자
        String otherArgsJson = IntStream.range(0, args.length)
                .filter(i -> !(args[i] instanceof HttpServletRequest))
                .filter(i -> !(args[i] instanceof CustomUserDetails))
                .filter(i -> !(args[i] instanceof org.springframework.validation.BindingResult))
                .mapToObj(i -> {
                    String name = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
                    Object arg = args[i];
                    try {
                        String value = objectMapper.writeValueAsString(arg);
                        if (value.length() > 200) value = value.substring(0, 200) + "...(truncated)";
                        return name + "=" + value;
                    } catch (Exception e) {
                        return name + "=" + String.valueOf(arg);
                    }
                })
                .collect(Collectors.joining(", "));

        String argsJson = userIdLog + (otherArgsJson.isEmpty() ? "" : ", " + otherArgsJson);

        log.info("REQUEST {} {} args={}", method, uri, argsJson);

        try {
            Object result = joinPoint.proceed();
            long time = System.currentTimeMillis() - start;

            String resultJson;
            try {
                resultJson = objectMapper.writeValueAsString(result);
                if (resultJson.length() > 600) {
                    resultJson = resultJson.substring(0, 600) + "...(truncated)";
                }
            } catch (Exception e) {
                resultJson = String.valueOf(result);
            }

            log.info("RESPONSE {} {} ({} ms) result={}", method, uri, time, resultJson);
            return result;
        } catch (Throwable e) {
            long time = System.currentTimeMillis() - start;
            log.error("EXCEPTION {} {} ({} ms) cause={}", method, uri, time, e.getMessage(), e);
            throw e;
        }
    }
}
