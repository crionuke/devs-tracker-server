package com.crionuke.devstracker.controllers.dto;

public class ErrorResponse {
    private final String id;
    private final String message;

    public ErrorResponse(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
