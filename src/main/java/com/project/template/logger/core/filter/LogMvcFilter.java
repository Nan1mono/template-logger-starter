package com.project.template.logger.core.filter;

import com.project.template.logger.core.repository.TemplateLogRepository;
import com.project.template.logger.entity.TemplateLog;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@Slf4j
@ComponentScan(basePackages = "com.project.template.logger.core.filter")
public class LogMvcFilter implements Filter {

    private boolean enable;

    private boolean body;

    private TemplateLogRepository repository;

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext sc = filterConfig.getServletContext();
        WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);
        if (cxt == null){
            return;
        }
        String enableProperty = cxt.getEnvironment().getProperty("template.logger.jpa.enable");
        String bodyProperty = cxt.getEnvironment().getProperty("template.logger.jpa.body");
        if (StringUtils.isNotBlank(enableProperty)){
            this.enable = Boolean.parseBoolean(enableProperty);
        }
        if (StringUtils.isNotBlank(bodyProperty)){
            this.body = Boolean.parseBoolean(bodyProperty);
        }
        WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        repository = springContext.getBean(TemplateLogRepository.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!enable) {
            chain.doFilter(request, response);
            return;
        }
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(response)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        // 跳过所有js，css和ico资源
        if (requestURI.endsWith(".js") || requestURI.endsWith(".css") || requestURI.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }
        // 读取请求体
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response); // 通过 responseWrapper 进行包装
        long endTime = System.currentTimeMillis();
        // 创建日志对象
        TemplateLog templateLog = new TemplateLog();
        templateLog.setRequestIP(request.getRemoteAddr())
                .setRequestURL(requestURI)
                .setRequestTime(LocalDateTime.now())
                .setMethod(httpRequest.getMethod())
                .setContentType(request.getContentType())
                .setHttpStatus(httpResponse.getStatus())
                .setDuration(endTime - startTime);
        // 设置请求体
        if (body) {
            templateLog.setRequestBody(requestBody);
        }
        // 保存日志
        repository.save(templateLog);
    }

    @Bean
    public FilterRegistrationBean<LogMvcFilter> loggingFilter() {
        FilterRegistrationBean<LogMvcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogMvcFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
