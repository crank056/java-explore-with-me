package ru.practicum.ewmmain.events.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.events.EventService;
import ru.practicum.ewmmain.events.model.EventDto;
import ru.practicum.ewmmain.events.model.EventShortDto;
import ru.practicum.ewmmain.events.model.UpdateEventRequest;
import ru.practicum.ewmmain.exceptions.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
public class EventControllerUser {

    private final EventService eventService;

    public EventControllerUser(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> getAllFromUser(@PathVariable Long userId, int from, int size) throws NotFoundException {
        return eventService.getAllFromUser(userId, from, size);
    }

    @PatchMapping
    public EventDto refreshFromUser(@PathVariable Long userId, @RequestBody UpdateEventRequest eventRequest) {
        return eventService.refreshFromUser(userId, eventShortDto);
    }

    @PostMapping
    public EventDto createEventFromUser(@PathVariable Long userId, @RequestBody EventShortDto eventShortDto) {
        return eventService.createEventFromUser(userId, eventShortDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventFromIdByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventFromIdByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto cancelEventFromUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.cancelEventFromUser(userId, eventId);
    }
}
















}
