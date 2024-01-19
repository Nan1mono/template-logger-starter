package com.project.template.logger.core.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoggerException extends RuntimeException {

    public LoggerException(String message) {
        super(message);
    }

}
