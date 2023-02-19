package com.thuan.logging.errorLogging;

import com.thuan.logging.entities.ErrorLog;
import com.thuan.logging.entities.RequestLog;
import com.thuan.logging.util.Constants;
import com.thuan.logging.util.JsonUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class ErrorLoggingAspect {
    private static final int MSG_LIMIT = 255;

    @Pointcut("@annotation(com.thuan.logging.errorLogging.ErrorLogging)")
    public void errorLoggingPointcut() {}

    @Pointcut(
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)"
    )
    public void restMappings() {}

    @Around("restMappings()")
    public void logAroundRestMappings(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("@Around Before Method");

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestLog requestLog = null;

        if(requestAttributes != null) {
            requestLog = (RequestLog) requestAttributes.getAttribute(RequestLog.class.toString(), RequestAttributes.SCOPE_REQUEST);
        }

        if(requestLog != null) {
            // Get request info
            String requestURI = getRequestURI();

            // Get request arguments
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String methodName = methodSignature.getDeclaringTypeName();
            Method method = methodSignature.getMethod();
            String arguments = getArguments(joinPoint);

            requestLog.setUri(requestURI);
            requestLog.setArgs(arguments);
            requestLog.setMethodName(methodName);
        }

        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            throw e;
        } finally {
            System.out.println("@Around After Method");
        }
    }

    @AfterThrowing(pointcut = "errorLoggingPointcut()", throwing = "t")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable t) {
        System.out.println("@AfterThrowing Method");

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if(requestAttributes != null) {
            ErrorLog errorLog = (ErrorLog) requestAttributes.getAttribute(ErrorLog.class.toString(), RequestAttributes.SCOPE_REQUEST);

            if(errorLog != null && !errorLog.isHasThrown()) {
                // Mark the originator of exception
                errorLog.setHasThrown(true);

                // Method arguments
                String arguments = getArguments(joinPoint);

                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                Method method = methodSignature.getMethod();

                // @ErrorLogging type
                ErrorLogging methodErrorLogging = method.getAnnotation(ErrorLogging.class);
                String type = methodErrorLogging.type();

                // Error info
                String methodName = methodSignature.getDeclaringTypeName();
                String errorMessage = t.getMessage() != null ? t.getMessage() : "NULL";
                String exceptionClass = getExceptionName(t);
                String firstStackTrace = getFirstStackTrace(t);

                errorLog.setArgs(arguments);
                errorLog.setMethodName(methodName);
                errorLog.setExceptionClass(exceptionClass);
                errorLog.setMessage(truncateMessage(errorMessage));
                errorLog.setFirstStack(truncateMessage(firstStackTrace));
            }
        } else {
            // Handle null pointer for request attributes
            System.out.println("Request attributes is null!");
        }
    }

    private String getRequestURI() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toString();
    }

    private String getRequestId() {
        return (String) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(Constants.REQUEST_ID, RequestAttributes.SCOPE_REQUEST);
    }

    private String getArguments(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();

        StringBuilder str = new StringBuilder();
        str.append("{");
        int n = args.length;
        for(int i = 0; i < n; ++i) {
            str.append("\"");
            str.append(parameterNames[i]);
            str.append("\":");
            str.append(JsonUtil.toJson(args[i]));

            if(i < n - 1) {
                str.append(",");
            }
        }
        str.append("}");
        return str.toString();
    }

    private String getExceptionName(Throwable t) {
        String exceptionReference = t.getClass().getName();
        String[] split = exceptionReference.split("\\.");
        return split[split.length - 1];
    }

    private String getFirstStackTrace(Throwable t) {
        StackTraceElement firstStackTrace = null;
        if(t.getStackTrace().length > 0) {
            firstStackTrace = t.getStackTrace()[0];
        }
        return firstStackTrace != null ? firstStackTrace.toString() : "null";
    }

    private static String truncateMessage(String message) {
        if (message.length() < MSG_LIMIT) {
            return message;
        }

        return message.substring(0, MSG_LIMIT-3) + "...";
    }
}