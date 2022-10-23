package ru.practicum.ewmmain.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class EndpointHitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
