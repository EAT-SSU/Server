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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.log.annotation.LogMask;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ControllerLogAspect {

    private final ObjectMapper objectMapper;
    private final SlackErrorNotifier slackErrorNotifier;

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

        String userId = getUserIdFromSecurityContext();
        String deviceType = getDeviceTypeFromSecurityContext();

        String userLog = "userId=" + userId + ", deviceType=" + deviceType;

        // 나머지 요청 인자 조합
        String otherArgsJson = IntStream.range(0, args.length)
                .filter(i -> !(args[i] instanceof HttpServletRequest))
                .filter(i -> !(args[i] instanceof CustomUserDetails))
                .filter(i -> !(args[i] instanceof org.springframework.validation.BindingResult))
                .mapToObj(i -> {
                    String name = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
                    Object arg = args[i];
                    try {
                        String value;
                        if (arg != null) {
                            Map<String, Object> safeMap = toSafeMap(arg);
                            value = objectMapper.writeValueAsString(safeMap);
                        } else {
                            value = "null";
                        }
                        if (value.length() > 200) value = value.substring(0, 200) + "...(truncated)";
                        return name + "=" + value;
                    } catch (Exception e) {
                        return name + "=" + String.valueOf(arg);
                    }
                })
                .collect(Collectors.joining(", "));

        String argsJson = userLog + (otherArgsJson.isEmpty() ? "" : ", " + otherArgsJson);

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
            String causeMessage = getCauseMessage(e);
            String exceptionType = e.getClass().getSimpleName();
            log.error("EXCEPTION {} {} ({} ms) type={} cause={}", method, uri, time, exceptionType, causeMessage, e);

            slackErrorNotifier.notify(e, method, uri, userId, argsJson);
            throw e;
        }
    }

    private String getCauseMessage(Throwable e) {
        if (e instanceof BaseException baseException) {
            return baseException.getStatus().getMessage();
        }
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    }

    private String getUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return String.valueOf(userDetails.getId());
        }
        return "anonymous";
    }

    private String getDeviceTypeFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            return userDetails.getDeviceType() != null
                    ? userDetails.getDeviceType().name()
                    : "null";
        }
        return "unknown";
    }

    private Map<String, Object> toSafeMap(Object arg) {
        Map<String, Object> result = new HashMap<>();
        for (Field field : arg.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(arg);
                if (field.isAnnotationPresent(LogMask.class)) {
                    value = "***";
                }
                result.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                result.put(field.getName(), "ERROR");
            }
        }
        return result;
    }
}
