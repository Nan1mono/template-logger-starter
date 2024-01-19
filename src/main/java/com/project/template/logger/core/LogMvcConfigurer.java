package com.project.template.logger.core;

import com.project.template.logger.core.interceptor.LogMvcInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.project.template.logger")
public class LogMvcConfigurer implements WebMvcConfigurer {

    private LogMvcInterceptor logMvcInterceptor;

    @Autowired
    public void setInterceptor(LogMvcInterceptor logMvcInterceptor){
        this.logMvcInterceptor = logMvcInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logMvcInterceptor)
                .addPathPatterns("/**")//指定该类拦截的url
                .excludePathPatterns( "/static/**");//过滤静态资源
    }

}
