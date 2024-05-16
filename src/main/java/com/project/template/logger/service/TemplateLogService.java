package com.project.template.logger.service;

import com.project.template.logger.core.exception.LoggerException;
import com.project.template.logger.core.repository.TemplateLogRepository;
import com.project.template.logger.entity.TemplateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class TemplateLogService {

    private final TemplateLogRepository templateLogRepository;

    @Autowired
    public TemplateLogService(TemplateLogRepository templateLogRepository) {
        this.templateLogRepository = templateLogRepository;
    }

    public Page<TemplateLog> list(String uri, String method, String ip, Integer httpStatus, Integer page, Integer size) {
        if (ObjectUtils.isEmpty(page) || ObjectUtils.isEmpty(size) || page <= 0 || size <= 0) {
            throw new LoggerException("page num or size not compliant!");
        }
        TemplateLog templateLog = new TemplateLog();
        if (!ObjectUtils.isEmpty(uri)) {
            templateLog.setRequestURL(uri);
        }
        if (!ObjectUtils.isEmpty(method)) {
            templateLog.setMethod(method);
        }
        if (!ObjectUtils.isEmpty(ip)) {
            templateLog.setRequestIP(ip);
        }
        if (!ObjectUtils.isEmpty(httpStatus)) {
            templateLog.setHttpStatus(httpStatus);
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("requestURL", match -> match.contains().ignoreCase())
                .withMatcher("requestMethod", match -> match.contains().ignoreCase())
                .withMatcher("requestIP", match -> match.contains().ignoreCase())
                .withMatcher("httpStatus", ExampleMatcher.GenericPropertyMatcher::exact);
        Example<TemplateLog> example = Example.of(templateLog, matcher);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return templateLogRepository.findAll(example, pageRequest);
    }

}
