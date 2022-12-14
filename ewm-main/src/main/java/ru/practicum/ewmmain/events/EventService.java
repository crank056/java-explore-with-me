package ru.practicum.ewmmain.events;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.categories.CategoryRepository;
import ru.practicum.ewmmain.client.EventClient;
import ru.practicum.ewmmain.events.model.*;
import ru.practicum.ewmmain.events.repositories.EventRepository;
import ru.practicum.ewmmain.events.repositories.LocationRepository;
import ru.practicum.ewmmain.exceptions.NotFoundException;
import ru.practicum.ewmmain.exceptions.StateException;
import ru.practicum.ewmmain.exceptions.ValidationException;
import ru.practicum.ewmmain.exceptions.WrongTimeException;
import ru.practicum.ewmmain.users.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final EventClient eventClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository,
                        LocationRepository locationRepository, UserRepository userRepository, EventClient eventClient) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.eventClient = eventClient;
    }

    public List<EventShortDto> getAllPublic(String text, Long[] categories,
                                            Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable,
                                            String sort, int from, int size, HttpServletRequest request) {
        List<Event> eventList;
        LocalDateTime start;
        LocalDateTime end;
        Pageable page = PageRequest.of(
                from / size, size, Sort.by(sort.equals("EVENT_DATE") ? "eventDate" : "views").ascending());
        if (rangeEnd == null && rangeStart == null) {
            start = LocalDateTime.now();
            eventList = eventRepository.findAllByEventDateIsAfterAndState(start, State.PUBLISHED, page).getContent();
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
            eventList = eventRepository.findAllByEventDateBetweenAndState(start, end, State.PUBLISHED, page).getContent();
        }
        if (text != null) {
            eventList = eventList.stream()
                    .filter(event -> event.getDescription().toLowerCase()
                            .contains(text.toLowerCase()) || event.getAnnotation().toLowerCase()
                            .contains(text.toLowerCase())).collect(Collectors.toList());
        }
        if (categories != null) {
            List<Long> categoryId = List.of(categories);
            eventList = eventList.stream().filter(event -> categoryId.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (paid != null) {
            if (paid) {
                eventList = eventList.stream().filter(Event::getPaid).collect(Collectors.toList());
            } else {
                eventList = eventList.stream()
                        .filter(event -> !event.getPaid())
                        .collect(Collectors.toList());
            }
        }
        if (onlyAvailable) {
            eventList = eventList.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        eventClient.sendToStatistics(new EndpointHitDto(
                "EWM", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(
                formatter)));
        return eventList.stream()
                .map(EventMapper::toShort)
                .collect(Collectors.toList());
    }

    public EventDto getFromIdPublic(Long id, HttpServletRequest request) throws NotFoundException, StateException {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("???? ??????????????");
        }
        Event event = eventRepository.getReferenceById(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new StateException("???? ????????????????????????");
        }
        ResponseEntity<Object> responseEntity = eventClient.sendToStatistics(new EndpointHitDto(
                "EWM", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(formatter)));
        if (responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        return EventMapper.toDto(eventRepository.getReferenceById(id));
    }

    public EventDto publishEventAdmin(Long eventId) throws NotFoundException, StateException, ValidationException {
        existAndNotPublishedEvent(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("?????????????????????? ???? ?????????? ???????????????? ?????????? ?????? ???? ?????? ???? ?????????????? ????????????????????");
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return EventMapper.toDto(eventRepository.save(event));
    }

    public EventDto rejectEventAdmin(Long eventId) throws NotFoundException, StateException {
        existAndNotPublishedEvent(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        event.setState(State.CANCELED);
        return EventMapper.toDto(eventRepository.save(event));
    }

    public EventDto editEventAdmin(Long eventId, AdminUpdateEventRequest updateEventRequest)
            throws NotFoundException {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("?????????????????????? ???? ????????????????????");
        }
        Event event = eventRepository.getReferenceById(eventId);
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategory()));
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), formatter));
        }
        Location location;
        if (updateEventRequest.getLocation() != null) {
            location = locationRepository.save(updateEventRequest.getLocation());
            event.setLocation(location);
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        return EventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getAllByAdmin(Long[] users, String[] states,
                                        Long[] categories, String rangeStart,
                                        String rangeEnd, int from, int size) {
        List<Event> eventList;
        LocalDateTime start;
        LocalDateTime end;
        Pageable page = PageRequest.of(from / size, size);
        if (rangeStart == null) {
            eventList = eventRepository.findAll();
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
            if (rangeEnd != null) {
                end = LocalDateTime.parse(rangeEnd, formatter);
                eventList = eventRepository.findAllByEventDateIsAfterAndEventDateIsBefore(start, end, page).getContent();
            } else {
                eventList = eventRepository.findAllByEventDateIsAfter(start, page).getContent();
            }
        }

        if (users != null) {
            List<Long> usersList = Arrays.asList(users);
            eventList = eventList.stream()
                    .filter(event -> usersList.contains(event.getInitiator().getId()))
                    .collect(Collectors.toList());
        }
        if (states != null) {
            List<String> statesList = Arrays.asList(states);
            eventList = eventList.stream()
                    .filter(event -> statesList.contains(event.getState().toString()))
                    .collect(Collectors.toList());
        }
        if (categories != null) {
            List<Long> categoriesList = Arrays.asList(categories);
            eventList = eventList.stream()
                    .filter(event -> categoriesList.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        return eventList.stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EventShortDto> getAllFromUser(Long userId, int from, int size) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("???????????????????????? ????????????????????????");
        }
        Pageable page = PageRequest.of(from / size, size);
        List<Event> eventList = eventRepository.findAllByInitiatorId(userId, page).getContent();
        return eventList.stream()
                .map(EventMapper::toShort)
                .collect(Collectors.toList());
    }

    public EventDto refreshFromUser(Long userId, UpdateEventRequest updateEventRequest)
            throws NotFoundException, StateException, WrongTimeException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("???????????????????????? ????????????????????????");
        }
        existAndNotPublishedEvent(updateEventRequest.getEventId());
        Event event = eventRepository.getReferenceById(updateEventRequest.getEventId());
        if (event.getState().equals(State.PUBLISHED)) {
            throw new StateException("???????????? ????????????????????????, ???????????????????????????? ????????????????????");
        }
        if (LocalDateTime.parse(updateEventRequest.getEventDate(), formatter)
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongTimeException(
                    "???????? ???????????? ???? ???????????? ?????? ?????????? 2 ????????");
        }
        event.setState(State.PENDING);
        event.setAnnotation(updateEventRequest.getAnnotation());
        event.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategory()));
        event.setDescription(updateEventRequest.getDescription());
        event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), formatter));
        event.setPaid(updateEventRequest.isPaid());
        event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        event.setTitle(updateEventRequest.getTitle());
        return EventMapper.toDto(eventRepository.save(event));
    }

    public EventDto createEventFromUser(Long userId, NewEventDto newEventDto) throws WrongTimeException {
        LocalDateTime time = LocalDateTime.parse(newEventDto.getEventDate(), formatter);
        if (time.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongTimeException(
                    "???????????? ???? ?????????? ???????? ?????????? ?????? ?????????? 2 ????????");
        }
        return EventMapper.toDto(eventRepository.save(EventMapper.fromNewToEntity(userId,
                newEventDto, locationRepository, categoryRepository, userRepository)));
    }

    public EventDto getEventFromIdByUser(Long userId, Long eventId) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("???????????????????????? ????????????????????????");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("?????????????????????? ???? ????????????????????");
        }
        return EventMapper.toDto(eventRepository.getReferenceById(eventId));
    }

    public EventDto cancelEventFromUser(Long userId, Long eventId) throws StateException, NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("???????????????????????? ????????????????????????");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("?????????????????????? ???? ????????????????????");
        }
        Event event = eventRepository.getReferenceById(eventId);
        if (!event.getState().equals(State.PENDING)) {
            throw new StateException("???????????????? ?????????????? ?????????? ???????????? ?? ?????????????? ??????????????????");
        }
        event.setState(State.CANCELED);
        return EventMapper.toDto(eventRepository.save(event));
    }

    private void existAndNotPublishedEvent(Long eventId) throws StateException, NotFoundException {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("?????????????????????? ???? ????????????????????");
        }
        if (eventRepository.getReferenceById(eventId).getState().equals(State.PUBLISHED)) {
            throw new StateException("?????? ????????????????????????");
        }
    }
}
