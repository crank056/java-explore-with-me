package ru.practicum.ewmstat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStats {
    String app;
    String uri;
    Long hits;
}
