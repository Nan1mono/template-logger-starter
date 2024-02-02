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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@ComponentScan(basePackages = "com.project.template.logger.core.filter")
public class LogJpaMvcFilter implements Filter {

    private boolean enable = true;

    private boolean body = false;

    private List<String> excluded = new ArrayList<>();

    private String enableProperty;

    private String bodyProperty;

    private TemplateLogRepository repository;

    private final PathMatcher matcher = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) {
        // 避免重复初始化
        if (StringUtils.isBlank(enableProperty)) {
            ServletContext sc = filterConfig.getServletContext();
            WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);
            if (cxt == null) {
                return;
            }
            enableProperty = cxt.getEnvironment().getProperty("template.logger.jpa.enable");
            String excludedStr = cxt.getEnvironment().getProperty("template.logger.jpa.excluded");
            if (StringUtils.isNotBlank(excludedStr)) {
                try {
                    excluded = List.of(excludedStr.split(","));
                } catch (Exception e) {
                    log.error("split char is not ',', excluded is empty");
                    excluded = Collections.emptyList();
                }
            }
            if (StringUtils.isBlank(bodyProperty)) {
                bodyProperty = cxt.getEnvironment().getProperty("template.logger.jpa.body");
            }
        }
        if (StringUtils.isNotBlank(enableProperty)) {
            this.enable = Boolean.parseBoolean(enableProperty);
        }
        if (StringUtils.isNotBlank(bodyProperty)) {
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
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(httpResponse);
        String requestURI = httpRequest.getRequestURI();
        // 跳过所有js，css和ico资源
        if (requestURI.endsWith(".js") || requestURI.endsWith(".css") || requestURI.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }
        // 跳过配置文件中已经排除的路由
        if (!CollectionUtils.isEmpty(excluded) && excluded.stream().anyMatch(t -> matcher.match(t, requestURI))) {
            chain.doFilter(request, response);
            return;
        }
        TemplateLog templateLog = TemplateLog.packageTemplate(request, response, chain, body);
        // 保存日志
        repository.save(templateLog);
        // 回写响应
        contentCachingResponseWrapper.copyBodyToResponse();
    }

    @Bean
    public FilterRegistrationBean<LogJpaMvcFilter> jpaLoggerFilter() {
        FilterRegistrationBean<LogJpaMvcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(this);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}