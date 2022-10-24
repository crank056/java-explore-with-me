package ru.practicum.ewmmain.requests.model;

public class RequestMapper {

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
