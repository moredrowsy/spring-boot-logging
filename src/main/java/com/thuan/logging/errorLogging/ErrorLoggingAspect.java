package com.thuan.logging.errorLogging;

import com.thuan.logging.config.GlobalMap;
import com.thuan.logging.entities.ErrorLog;
import com.thuan.logging.entities.RequestLog;
import com.thuan.logging.util.Constants;
import com.thuan.logging.util.JsonUtil;
import com.thuan.logging.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Before("restMappings()")
    public void logBefore(JoinPoint joinPoint) throws Throwable {
        String isLogging = GlobalMap.get("logging");

        if(isLogging.equals("true")) {
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
                String className = methodSignature.getDeclaringTypeName();
                String shortClassName = getShortClassName(className);
                String methodName = methodSignature.getName();
                String arguments = getArguments(joinPoint);

                requestLog.setUri(requestURI);
                requestLog.setClassName(shortClassName);
                requestLog.setMethodName(methodName);
                requestLog.setArgs(StringUtils.truncateMessage(arguments, Constants.ARGS_LIMIT));
            }
        }
    }

    @AfterThrowing(pointcut = "errorLoggingPointcut()", throwing = "t")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable t) {
        String isLogging = GlobalMap.get("logging");

        if(isLogging.equals("true")) {
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
                    String className = methodSignature.getDeclaringTypeName();
                    String shortClassName = getShortClassName(className);
                    String methodName = methodSignature.getName();
                    String errorMessage = t.getMessage() != null ? t.getMessage() : "NULL";
                    String exceptionClass = getExceptionName(t);
                    String firstStackTrace = getFirstStackTrace(t);

                    errorLog.setClassName(shortClassName);
                    errorLog.setMethodName(methodName);
                    errorLog.setArgs(StringUtils.truncateMessage(arguments, Constants.ARGS_LIMIT));
                    errorLog.setExceptionClass(exceptionClass);
                    errorLog.setMessage(StringUtils.truncateMessage(errorMessage, Constants.MSG_LIMIT));
                    errorLog.setFirstStack(StringUtils.truncateMessage(firstStackTrace, Constants.MSG_LIMIT));
                }
            } else {
                // TODO Handle null pointer for request attributes
                System.out.println("Request attributes is null!");
            }
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

    private String getShortClassName(String className) {
        String[] split = className.split("\\.");
        return split[split.length-1];
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
}