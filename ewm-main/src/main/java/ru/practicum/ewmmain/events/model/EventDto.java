package ru.practicum.ewmmain.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.categories.model.CategoryDto;
import ru.practicum.ewmmain.users.model.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String tittle;
    private String annotations;
    private CategoryDto category;
    private String description;
    private LocalDateTime created;
    private LocalDateTime event_date;
    private LocalDateTime published;
    private Location location_id;
    private Boolean paid;
    private Integer participant_limit;
    private Boolean request_moderation;
    private Integer confirmed_requests;
    private UserShortDto initiator;
    private String state;
    private Long views;
}
