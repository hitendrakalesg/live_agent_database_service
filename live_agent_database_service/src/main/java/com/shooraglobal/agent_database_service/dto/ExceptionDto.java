package com.shooraglobal.agent_database_service.dto;

import java.time.LocalDateTime;

public class ExceptionDto {

    private String message;

    private int status;

    private LocalDateTime timestamp;

    public ExceptionDto() {
    }

    public ExceptionDto(
            String message,
            int status,
            LocalDateTime timestamp
    ) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}