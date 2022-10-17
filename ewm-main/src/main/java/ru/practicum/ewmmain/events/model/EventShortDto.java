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
public class EventShortDto {
    private Long id;
    private String tittle;
    private String annotations;
    private CategoryDto category;
    private String description;
    private LocalDateTime event_date;
    private Boolean paid;
    private Integer confirmed_requests;
    private UserShortDto initiator;
    private Long views;
}
