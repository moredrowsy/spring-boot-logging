package com.thuan.logging.errorLogging;

import com.thuan.logging.entities.ErrorLog;
import com.thuan.logging.entities.RequestLog;
import com.thuan.logging.services.ErrorLogService;
import com.thuan.logging.services.RequestLogService;
import com.thuan.logging.util.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ErrorLoggingInterceptor implements HandlerInterceptor {

    private final ErrorLogService errorLogService;
    private final RequestLogService requestLogService;

    ErrorLoggingInterceptor(ErrorLogService errorLogService, RequestLogService requestLogService) {
        this.errorLogService = errorLogService;
        this.requestLogService = requestLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // Guard against Interceptor running twice in same request thread
        ErrorLog previousErrorLog = (ErrorLog) request.getAttribute(ErrorLog.class.toString());
        RequestLog previousRequestLog = (RequestLog) request.getAttribute(RequestLog.class.toString());
        if(previousErrorLog != null || previousRequestLog != null) {
            return false;
        }

        // Get header values
        HttpHeaders httpHeaders = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(request.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new
                ));

        String requestId = UUID.randomUUID().toString();
        RequestLog requestLog = new RequestLog();
        requestLog.setRequestId(requestId);
        ErrorLog errorLog = new ErrorLog();
        errorLog.setRequestId(requestId);

        request.setAttribute(Constants.REQUEST_START_TIME , System.currentTimeMillis());
        request.setAttribute(RequestLog.class.toString(), requestLog);
        request.setAttribute(ErrorLog.class.toString(), errorLog);

        return true;
    }

//    @Override
//    public void postHandle( HttpServletRequest request,
//                            HttpServletResponse response,
//                            Object handler,
//                            ModelAndView modelAndView) throws Exception {
//        long responseTime = System.currentTimeMillis() - (long) request.getAttribute(Constants.REQUEST_START_TIME);
//        System.out.println("Response time in ms: " + responseTime);
//    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception exception) throws Exception {
        long completionTime = System.currentTimeMillis() - (long) request.getAttribute(Constants.REQUEST_START_TIME);

        ErrorLog errorLog = (ErrorLog) request.getAttribute(ErrorLog.class.toString());
        RequestLog requestLog = (RequestLog) request.getAttribute(RequestLog.class.toString());
        requestLog.setStatus(String.valueOf(response.getStatus()));
        requestLog.setCompletionTime(completionTime);

        try {
            if(errorLog.isHasThrown()) {
                errorLogService.save(errorLog);
            }
            requestLogService.save(requestLog);
        } catch (Exception e) {
            // TODO
            System.out.println("TODO for handling repository errors : " + e);
        }
    }
}