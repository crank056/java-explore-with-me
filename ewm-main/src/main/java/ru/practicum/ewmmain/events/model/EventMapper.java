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
                event.getEventDate(),
                event.getPaid(),
                event.getConfirmedRequests(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getViews()
        );
    }

    public static EventDto toDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getTittle(),
                event.getAnnotations(),
                CategoryMapper.toDto(event.getCategory()),
                event.getDescription(),
                event.getCreated(),
                event.getEventDate(),
                event.getPublished(),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getConfirmedRequests(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getState(),
                event.getViews()
        );
    }
}
