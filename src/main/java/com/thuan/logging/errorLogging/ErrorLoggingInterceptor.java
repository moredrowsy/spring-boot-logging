package com.thuan.logging.errorLogging;

import com.thuan.logging.config.GlobalMap;
import com.thuan.logging.entities.ErrorLog;
import com.thuan.logging.entities.RequestLog;
import com.thuan.logging.services.ErrorLogService;
import com.thuan.logging.services.RequestLogService;
import com.thuan.logging.util.Constants;
import com.thuan.logging.util.JsonUtil;
import com.thuan.logging.util.StringUtils;
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

    private final GlobalMap globalMap;

    ErrorLoggingInterceptor(ErrorLogService errorLogService, RequestLogService requestLogService, GlobalMap globalMap) {
        this.errorLogService = errorLogService;
        this.requestLogService = requestLogService;
        this.globalMap = globalMap;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String isLogging = GlobalMap.get("logging");

        if(isLogging.equals("true")) {// Guard against Interceptor running twice in same request thread
            ErrorLog previousErrorLog = (ErrorLog) request.getAttribute(ErrorLog.class.toString());
            RequestLog previousRequestLog = (RequestLog) request.getAttribute(RequestLog.class.toString());
            if (previousErrorLog != null || previousRequestLog != null) {
                return false;
            }

            request.setAttribute(Constants.REQUEST_START_TIME, System.currentTimeMillis());

            String requestId = UUID.randomUUID().toString();

            // Get header values
            HttpHeaders httpHeaders = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            h -> Collections.list(request.getHeaders(h)),
                            (oldValue, newValue) -> newValue,
                            HttpHeaders::new
                    ));
            String headers = JsonUtil.toJson(httpHeaders);

            RequestLog requestLog = new RequestLog();
            requestLog.setRequestId(requestId);
            requestLog.setHeaders(StringUtils.truncateMessage(headers, Constants.HEADER_LIMIT));

            ErrorLog errorLog = new ErrorLog();
            errorLog.setRequestId(requestId);

            request.setAttribute(RequestLog.class.toString(), requestLog);
            request.setAttribute(ErrorLog.class.toString(), errorLog);
        }

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
        String isLogging = GlobalMap.get("logging");

        if(isLogging.equals("true")) {
            if(response.getStatus() == 404) {
                return;
            }

            ErrorLog errorLog = (ErrorLog) request.getAttribute(ErrorLog.class.toString());
            RequestLog requestLog = (RequestLog) request.getAttribute(RequestLog.class.toString());
            requestLog.setStatus(String.valueOf(response.getStatus()));

            try {
                if(errorLog.isHasThrown()) {
                    errorLogService.save(errorLog);
                }

                long completionTime = System.currentTimeMillis() - (long) request.getAttribute(Constants.REQUEST_START_TIME);
                requestLog.setCompletionTime(completionTime);

                requestLogService.save(requestLog);
            } catch (Throwable t) {
                // TODO
                System.out.println("TODO for handling repository errors : " + t);
            }
        }
    }
}