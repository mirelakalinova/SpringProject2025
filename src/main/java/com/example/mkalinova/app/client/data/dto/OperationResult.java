package com.example.mkalinova.app.client.data.dto;

public class OperationResult {
    private boolean success;
    private String message;
//todo -> check usage
    public OperationResult() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
