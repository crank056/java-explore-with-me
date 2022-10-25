package ru.practicum.ewmmain.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends Exception {
    private String ObjClass;
    private long id;
    public NotFoundException(String message) {
        super(message);
    }
}
