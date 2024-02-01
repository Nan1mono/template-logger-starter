package com.project.template.logger.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class TemplateLog {

    private String requestIP;

    private String requestURL;

    private LocalDateTime requestTime;

    private String method;

    private String contentType;

    private String headers;

    private String requestBody;

    private String enumeration;

    private Integer httpStatus;

    private String responseBody;

    private Long duration;

}
