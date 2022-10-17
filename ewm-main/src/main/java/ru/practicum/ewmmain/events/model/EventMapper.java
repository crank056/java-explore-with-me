package ru.practicum.ewmmain.events.model;

import ru.practicum.ewmmain.categories.model.CategoryMapper;
import ru.practicum.ewmmain.users.model.UserMapper;

public class EventMapper {

    public static EventShortDto toShort(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getTittle(),
                event.getAnnotations(),
                CategoryMapper.toDto(event.getCategory()),
                event.getDescription(),
                event.getEvent_date(),
                event.getPaid(),
                event.getConfirmed_requests(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getViews()
        );
    }
}
