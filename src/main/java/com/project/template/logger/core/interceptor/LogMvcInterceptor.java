package com.project.template.logger.core.interceptor;

import com.project.template.logger.core.exception.LoggerException;
import com.project.template.logger.entity.TemplateLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@ComponentScan(basePackages = "com.project.template.logger")
@Slf4j
public class LogMvcInterceptor implements HandlerInterceptor {

    @Resource
    private MongoTemplate mongoTemplate;

    @Value("${template.logger.mongo.enable:false}")
    private boolean enable;

    @Value("${template.logger.mongo.body:false}")
    private boolean body;

    @Override
    public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) throws Exception {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }


    @Override
    public void postHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler,
                           ModelAndView modelAndView) throws IOException {
        if (!enable) {
            return;
        }
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(response)) {
            throw new LoggerException();
        }
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith(".js") || requestURI.endsWith(".css") || requestURI.endsWith(".ico")) {
            return;
        }
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        // 获取相应信息
        TemplateLog templateLog = new TemplateLog();
        templateLog.setRequestIP(request.getRemoteAddr())
                .setRequestURL(requestURI)
                .setRequestTime(LocalDateTime.now())
                .setMethod(request.getMethod())
                .setContentType(request.getContentType())
                .setHttpStatus(response.getStatus())
                .setResponseBody("responseBody")
                .setDuration(endTime - startTime);
        if (body) {
            templateLog.setRequestBody(requestBody);
        }
        mongoTemplate.insert(templateLog);
    }

}
