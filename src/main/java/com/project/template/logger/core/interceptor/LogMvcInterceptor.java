package com.project.template.logger.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LogMvcInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response,
                           @Nullable Object handler, ModelAndView modelAndView) {
        // Perform actions after request processing
    }

}
