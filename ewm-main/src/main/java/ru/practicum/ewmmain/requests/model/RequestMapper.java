package ru.practicum.ewmmain.requests.model;

import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.model.EventMapper;
import ru.practicum.ewmmain.users.model.User;
import ru.practicum.ewmmain.users.model.UserMapper;

public class RequestMapper {

    public static Request toEntity(RequestDto requestDto, Event event, User user) {
        return new Request(
                requestDto.getId(),
                requestDto.getCreated(),
                event,
                user,
                requestDto.getStatus()
        );
    }

    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                EventMapper.toShort(request.getEvent()),
                UserMapper.toUserShortDto(request.getRequester()),
                request.getStatus()
        );
    }

    public static ParticipationRequestDto toPRDto(Request request) {
        return new ParticipationRequestDto(
            request.getId(),
            request.getCreated().toString(),
            request.getEvent().getId(),
            request.getRequester().getId(),
            request.getStatus().toString()
        );
    }
}
