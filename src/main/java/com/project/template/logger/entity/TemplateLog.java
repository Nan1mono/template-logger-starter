package com.project.template.logger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

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

}
