package ru.practicum.ewmmain.requests;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.requests.model.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/events/{eventId}/requests")
    public List<RequestDto> getAllRequestFromUserEvent(@PathVariable long userId,
                                                       @PathVariable long eventId) {
        return requestService.getAllRequestFromUserEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(
            @PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId) {
        return requestService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(
            @PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId) {
        return requestService.rejectRequest(userId, eventId, reqId);
    }

    @GetMapping("/requests")
    public List<RequestDto> getAllUserRequest(@PathVariable long userId) {
        return requestService.getAllUserRequest(userId);
    }

    @PostMapping("/requests")
    public RequestDto createRequest(@PathVariable long userId, @RequestParam long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
