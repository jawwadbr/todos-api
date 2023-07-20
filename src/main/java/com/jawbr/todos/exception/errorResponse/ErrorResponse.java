package com.jawbr.todos.exception.errorResponse;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorResponse {

    private int status;
    private String message;
    private String timeStamp;

    public ErrorResponse(int status, String message, long timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = formatTimestamp(timeStamp);
    }

    private String formatTimestamp(long timeStamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timeStamp), java.time.ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        return dateTime.format(formatter);
    }
}
