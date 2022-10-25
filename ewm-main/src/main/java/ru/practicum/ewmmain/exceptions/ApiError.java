package ru.practicum.ewmmain.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
    private StackTraceElement[] errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
