package com.moncreneau.exceptions;

import java.util.Map;

public class MoncreneauException extends RuntimeException {
    private final String errorCode;
    private final int statusCode;
    private final Map<String, Object> details;

    @SuppressWarnings("unchecked")
    public MoncreneauException(Map<String, Object> error, int statusCode) {
        super((String) error.getOrDefault("message", "An error occurred"));
        this.errorCode = (String) error.getOrDefault("code", "UNKNOWN_ERROR");
        this.statusCode = statusCode;
        this.details = (Map<String, Object>) error.get("details");
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (HTTP %d)", errorCode, getMessage(), statusCode);
    }
}
