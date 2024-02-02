package com.project.template.logger.core.repository;

import com.project.template.logger.entity.TemplateLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateLogRepository extends JpaRepository<TemplateLog, Long> {
}
