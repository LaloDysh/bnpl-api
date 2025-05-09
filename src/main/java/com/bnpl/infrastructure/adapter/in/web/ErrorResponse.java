package com.bnpl.infrastructure.adapter.in.web;

public class ErrorResponse {
    private String code;
    private String error;
    private long timestamp;
    private String message;
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String error, long timestamp, String message, String path) {
        this.code = code;
        this.error = error;
        this.timestamp = timestamp;
        this.message = message;
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}