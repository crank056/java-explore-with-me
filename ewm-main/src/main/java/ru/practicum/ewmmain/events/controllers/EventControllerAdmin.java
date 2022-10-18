package ru.practicum.ewmmain.events.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.events.EventService;
import ru.practicum.ewmmain.events.model.EventDto;
import ru.practicum.ewmmain.events.model.EventShortDto;

import java.util.List;

@RestController
@RequestMapping("admin/events")
public class EventControllerAdmin {

    private final EventService eventService;

    public EventControllerAdmin(EventService eventService) {
        this.eventService = eventService;
    }

    @PatchMapping("/{eventId}/publish")
    public EventDto publishEvent(@PathVariable Long eventId) {
        return eventService.publishEventAdmin(eventId);
    }

    @GetMapping("/{eventId}/reject")
    public EventDto rejectEvent(@PathVariable Long eventId) {
        return eventService.rejectEventAdmin(eventId);
    }

    @PutMapping("/{eventId}")
    public EventShortDto editEvent(@PathVariable Long eventId, @RequestBody EventShortDto eventShortDto) {
        return eventService.editEventAdmin(eventId, eventShortDto);
    }

    @GetMapping
    public List<EventDto> getAll(@RequestParam(required = false) Long[] users,
                                 @RequestParam(required = false) String[] states,
                                 @RequestParam(required = false) Long[] categories,
                                 @RequestParam(required = false) String rangeStart,
                                 @RequestParam(required = false) String rangeEnd,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
