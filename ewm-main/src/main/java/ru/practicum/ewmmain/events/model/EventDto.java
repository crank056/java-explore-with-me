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
    private LocalDateTime eventDate;
    private LocalDateTime published;
    private Location locationId;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private Integer confirmedRequests;
    private UserShortDto initiator;
    private State state;
    private Long views;
}
