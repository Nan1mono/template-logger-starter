package com.project.template.logger.entity;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "template.logger")
@Slf4j
public class TemplateLogConfig {

    private boolean enable = true;
    private String level = "INFO";
    private FileProperties file = new FileProperties();

    @Getter
    @Setter
    protected static class FileProperties {
        private boolean enable = false;
        private String name = "info.log";
        private String path = "./log";
        private int history = 30;
        private String size = "10MB";

    }

    @PostConstruct
    public void init() {
        // 这里编写在 Spring Bean 初始化后要执行的逻辑
        log.info("Template logger init success");
        log.info("Template logger level:{}, file enable:{}, file name:{}, path:{},history:{}, size:{}", level, file.enable, file.name, file.path, file.history, file.size);
    }
}
