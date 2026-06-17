package com.shooraglobal.agent_database_service.exception;

public class AgentDatabaseServiceException extends RuntimeException {

    public AgentDatabaseServiceException(String message) {
        super(message);
    }

    public AgentDatabaseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
