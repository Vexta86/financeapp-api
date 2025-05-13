package com.api.financeapp.dtos;
import java.time.LocalDateTime;

public record ErrorDTO(
        String error,
        String message,
        int status,
        LocalDateTime timestamp
) {
    public ErrorDTO(String error, String message, int status) {
        this(error, message, status, LocalDateTime.now());
    }
}
