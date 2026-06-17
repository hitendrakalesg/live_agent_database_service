package com.shooraglobal.agent_database_service.exception;


import com.shooraglobal.agent_database_service.dto.ExceptionDto;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AgentDatabaseServiceException.class)
    public ResponseEntity<ExceptionDto> handleAgentDatabaseServiceException(
            AgentDatabaseServiceException ex
    ) {

        ExceptionDto dto = new ExceptionDto();

        dto.setMessage(ex.getMessage());
        dto.setStatus(HttpStatus.BAD_REQUEST.value());
        dto.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(
                dto,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleException(
            Exception ex
    ) {

        ExceptionDto dto = new ExceptionDto();

        dto.setMessage(ex.getMessage());
        dto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        dto.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(
                dto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
