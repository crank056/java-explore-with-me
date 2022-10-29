package ru.practicum.ewmmain.requests;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.exceptions.AccessException;
import ru.practicum.ewmmain.exceptions.NotFoundException;
import ru.practicum.ewmmain.exceptions.ValidationException;
import ru.practicum.ewmmain.requests.model.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestFromUserEvent(@PathVariable long userId,
                                                                    @PathVariable long eventId)
        throws AccessException, NotFoundException {
        return requestService.getAllRequestFromUserEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(
        @PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId)
        throws NotFoundException, AccessException, ValidationException {
        return requestService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(
        @PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId)
        throws AccessException, NotFoundException {
        return requestService.rejectRequest(userId, eventId, reqId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getAllUserRequest(@PathVariable long userId) throws NotFoundException {
        return requestService.getAllUserRequest(userId);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto createRequest(@PathVariable long userId, @RequestParam long eventId)
        throws AccessException, NotFoundException {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId)
        throws AccessException, NotFoundException {
        return requestService.cancelRequest(userId, requestId);
    }
}
