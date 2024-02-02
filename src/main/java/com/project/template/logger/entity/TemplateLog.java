package com.project.template.logger.entity;

import jakarta.persistence.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(name = "template_log")
@Table(name = "template_log")
@Accessors(chain = true)
public class TemplateLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Transient
    private Long id;

    @Column(name = "request_ip")
    private String requestIP;

    @Column(name = "request_url")
    private String requestURL;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "method")
    private String method;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "headers")
    private String headers;

    @Column(name = "request_body", columnDefinition = "text")
    private String requestBody;

    @Column(name = "enumeration", columnDefinition = "text")
    private String enumeration;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "response_body", columnDefinition = "text")
    private String responseBody;

    @Column(name = "duration")
    private Long duration;


    public static TemplateLog packageTemplate(ServletRequest request, ServletResponse response, FilterChain chain, boolean body) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(httpResponse);
        String requestURI = httpRequest.getRequestURI();
        long startTime = System.currentTimeMillis();
        chain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
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
            // 读取POST请求体
            String thisRequestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            // 读取GET请求体
            StringBuilder thisEnumeration = new StringBuilder();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                if (parameterNames.hasMoreElements()) {
                    thisEnumeration.append(String.format("%s=%s&", paramName, paramValue));
                } else {
                    thisEnumeration.append(String.format("%s=%s", paramName, paramValue));
                }
            }
            templateLog.setEnumeration(thisEnumeration.toString());
            templateLog.setRequestBody(thisRequestBody);
            // 获取响应体
            // 输出响应请求，如果请求响应是一个非文本，则跳过
            String thisContentType = contentCachingResponseWrapper.getContentType();
            if (thisContentType != null && (thisContentType.startsWith("text/") || thisContentType.startsWith("application/json"))) {
                byte[] contentAsByteArray = contentCachingResponseWrapper.getContentAsByteArray();
                String thisResponseBody = new String(contentAsByteArray, StandardCharsets.UTF_8);
                templateLog.setResponseBody(thisResponseBody);
            }
        }
        return templateLog;
    }

}
