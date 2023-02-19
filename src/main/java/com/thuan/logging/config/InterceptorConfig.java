package com.thuan.logging.config;

import com.thuan.logging.errorLogging.ErrorLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final ErrorLoggingInterceptor errorLoggingInterceptor;

    InterceptorConfig(ErrorLoggingInterceptor errorLoggingInterceptor) {
        this.errorLoggingInterceptor = errorLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor( errorLoggingInterceptor );

        // Can limit interceptor to specific path
        // registry.addInterceptor( new LogInterceptor() ).addPathPatterns("/student/**");
    }

    // Can specific interceptor order
    //@Override
    //public void addInterceptors(InterceptorRegistry registry) {
    //    registry.addInterceptor( new LogInterceptor() ).order(1);
    //    registry.addInterceptor( new AuthenticationInterceptor() ).order(2);
    //}

}