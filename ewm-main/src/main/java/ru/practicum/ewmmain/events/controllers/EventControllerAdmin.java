package ru.practicum.ewmmain.events.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.events.EventService;
import ru.practicum.ewmmain.events.model.AdminUpdateEventRequest;
import ru.practicum.ewmmain.events.model.EventDto;
import ru.practicum.ewmmain.events.model.EventShortDto;
import ru.practicum.ewmmain.exceptions.NotFoundException;
import ru.practicum.ewmmain.exceptions.StateException;
import ru.practicum.ewmmain.exceptions.ValidationException;

import java.util.List;

@RestController
@RequestMapping("admin/events")
public class EventControllerAdmin {

    private final EventService eventService;

    public EventControllerAdmin(EventService eventService) {
        this.eventService = eventService;
    }

    @PatchMapping("/{eventId}/publish")
    public EventDto publishEvent(@PathVariable Long eventId) throws ValidationException, NotFoundException, StateException {
        return eventService.publishEventAdmin(eventId);
    }

    @GetMapping("/{eventId}/reject")
    public EventDto rejectEvent(@PathVariable Long eventId) throws NotFoundException, StateException {
        return eventService.rejectEventAdmin(eventId);
    }

    @PutMapping("/{eventId}")
    public EventDto editEvent(@PathVariable Long eventId, @RequestBody AdminUpdateEventRequest updateEventRequest) throws NotFoundException {
        return eventService.editEventAdmin(eventId, updateEventRequest);
    }

    @GetMapping
    public List<EventDto> getAllAdmin(@RequestParam(required = false) Long[] users,
                                 @RequestParam(required = false) String[] states,
                                 @RequestParam(required = false) Long[] categories,
                                 @RequestParam(required = false) String rangeStart,
                                 @RequestParam(required = false) String rangeEnd,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
