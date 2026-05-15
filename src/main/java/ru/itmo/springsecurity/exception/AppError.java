package ru.itmo.springsecurity.exception;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppError {

    private int status;
    private String message;
    private LocalDate timestamp;

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDate.now();
    }
}
