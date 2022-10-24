package ru.practicum.ewmmain.events;

import lombok.RequiredArgsConstructor;
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
        List<Event> list;
        LocalDateTime start;
        LocalDateTime end;
        Pageable page = PageRequest.of(
                from / size, size, Sort.by(sort.equals("EVENT_DATE") ? "eventDate" : "views").ascending());
        if(rangeEnd == null && rangeStart == null) {
            start = LocalDateTime.now();
            list = eventRepository.findAllByEventDateIsAfterAndState(start, State.PUBLISHED, page).getContent();
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
            list = eventRepository.findAllByEventDateBetweenAndState(start, end, State.PUBLISHED, page).getContent();
        }
        if(text != null) {
            list = list.stream().filter(event -> event.getDescription().toLowerCase()
                    .contains(text.toLowerCase()) || event.getAnnotations().toLowerCase()
                    .contains(text.toLowerCase())).collect(Collectors.toList());
        }
        if(categories != null) {
            List<Long> categoryId = List.of(categories);
            list = list.stream().filter(event -> categoryId.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if(paid != null) {
            if(paid) {
                list = list.stream().filter(event -> event.getPaid()).collect(Collectors.toList());
            } else list = list.stream().filter(event -> !event.getPaid()).collect(Collectors.toList());
        }
        if(onlyAvailable) {
            list = list.stream().filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        eventClient.sendToStatistics(new EndpointHitDto(
                "EWM", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(
                    formatter)));
        return list.stream().map(event -> EventMapper.toShort(event)).collect(Collectors.toList());
    }

    public EventShortDto getFromIdPublic(Long id, HttpServletRequest request) throws NotFoundException, StateException {
        if (!eventRepository.existsById(id)) throw new NotFoundException("Не найдено");
        Event event = eventRepository.getReferenceById(id);
        if (!event.getState().equals(State.PUBLISHED)) throw new StateException("Не опубликовано");
        ResponseEntity<Object> responseEntity = eventClient.sendToStatistics(new EndpointHitDto(
                "EWM", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(formatter)));
        if (responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        return EventMapper.toShort(eventRepository.getReferenceById(id));
    }

    public EventDto publishEventAdmin(Long eventId) throws NotFoundException, StateException, ValidationException {
        existAndNotPublishedEvent(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new ValidationException("Мероприятие не может начаться ранее чем за час до времени публикации");
        event.setState(State.PUBLISHED);
        event.setPublished(LocalDateTime.now());
        return EventMapper.toDto(eventRepository.save(event));
    }

    public EventDto rejectEventAdmin(Long eventId) throws NotFoundException, StateException {
        existAndNotPublishedEvent(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        event.setState(State.CANCELED);
        return EventMapper.toDto(eventRepository.save(event));
    }

    public EventDto editEventAdmin(Long eventId, AdminUpdateEventRequest updateEventRequest) throws NotFoundException {
        if (!eventRepository.existsById(eventId)) throw new NotFoundException("Мероприятия не существует");
        Event event = eventRepository.getReferenceById(eventId);
        if (updateEventRequest.getAnnotation() != null)
            event.setAnnotations(updateEventRequest.getAnnotation());
        if (updateEventRequest.getCategory() != null)
            event.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategory()));
        if (updateEventRequest.getDescription() != null)
            event.setDescription(updateEventRequest.getDescription());
        if (updateEventRequest.getEventDate() != null)
            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), formatter));
        Location location = new Location();
        if (updateEventRequest.getLocation() != null) {
            location = locationRepository.findByLatAndLon(
                    updateEventRequest.getLocation().getLat(), updateEventRequest.getLocation().getLon());
        }
        if (location == null) location = locationRepository.save(updateEventRequest.getLocation());
        event.setLocation(location);
        if (updateEventRequest.getPaid() != null)
            event.setPaid(updateEventRequest.getPaid());
        if (updateEventRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        if (updateEventRequest.getRequestModeration() != null)
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        if (updateEventRequest.getTitle() != null)
            event.setTittle(updateEventRequest.getTitle());
        return EventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getAllAdmin(Long[] users, String[] states,
                                      Long[] categories, String rangeStart,
                                      String rangeEnd, int from, int size) {
        List<Event> list;
        LocalDateTime start;
        LocalDateTime end;
        Pageable page = PageRequest.of(from / size, size);
        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else start = LocalDateTime.parse(rangeStart, formatter);
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, formatter);
            list = eventRepository.findAllByEventDateIsAfterAndEventDateIsBefore(start, end, page).getContent();
        } else list = eventRepository.findAllByEventDateIsAfter(start, page).getContent();

        if (users != null) {
            List<Long> usersList = Arrays.asList(users);
            for (Event event : list) {
                if (!usersList.contains(event.getInitiator().getId())) list.remove(event);
            }
        }
        if (states != null) {
            List<String> statesList = Arrays.asList(states);
            for (Event event : list) {
                if (!statesList.contains(event.getState().toString())) list.remove(event);
            }
        }
        if (categories != null) {
            List<Long> categoriesList = Arrays.asList(categories);
            for (Event event : list) {
                if (!categoriesList.contains(event.getCategory().getId())) list.remove(event);
            }
        }
        return list.stream().map(event -> EventMapper.toDto(event)).collect(Collectors.toList());
    }

    public List<EventShortDto> getAllFromUser(Long userId, int from, int size) throws NotFoundException {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Пользователь несуществует");
        Pageable page = PageRequest.of(from / size, size);
        List<Event> list = eventRepository.findAllByInitiatorId(userId, page).getContent();
        return list.stream().map(event -> EventMapper.toShort(event)).collect(Collectors.toList());
    }

    public EventDto refreshFromUser(Long userId, UpdateEventRequest updateEventRequest)
            throws NotFoundException, StateException, WrongTimeException {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Пользователь несуществует");
        existAndNotPublishedEvent(updateEventRequest.getEventId());
        Event event = eventRepository.getReferenceById(updateEventRequest.getEventId());
        if (!event.getState().equals(State.CANCELED) || !event.getState().equals(State.PENDING))
            throw new StateException("Статус опубликовано, редактирование невозможно");
        if (LocalDateTime.parse(updateEventRequest.getEventDate(), formatter)
                .isBefore(LocalDateTime.now().plusHours(2))) throw new WrongTimeException(
                "Дата начала не раньше чем через 2 часа");
        event.setState(State.PENDING);
        event.setAnnotations(updateEventRequest.getAnnotation());
        event.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategory()));
        event.setDescription(updateEventRequest.getDescription());
        event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate()));
        event.setPaid(updateEventRequest.isPaid());
        event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        event.setTittle(updateEventRequest.getTitle());
        return EventMapper.toDto(eventRepository.save(event));
    }

    public EventDto createEventFromUser(Long userId, NewEventDto newEventDto) throws WrongTimeException {
        LocalDateTime time = LocalDateTime.parse(newEventDto.getEventDate());
        if (time.isAfter(LocalDateTime.now().plusHours(2))) throw new WrongTimeException(
                "Начало не может быть ранее чем через 2 часа");
        return EventMapper.toDto(eventRepository.save(EventMapper.fromNewToEntity(userId,
            newEventDto, locationRepository, categoryRepository, userRepository)));
    }

    public EventDto getEventFromIdByUser(Long userId, Long eventId) throws NotFoundException {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Пользователь несуществует");
        if (!eventRepository.existsById(eventId)) throw new NotFoundException("Мероприятие не существует");
        return EventMapper.toDto(eventRepository.getReferenceById(eventId));
    }

    public EventDto cancelEventFromUser(Long userId, Long eventId) throws StateException, NotFoundException {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Пользователь несуществует");
        if (!eventRepository.existsById(eventId)) throw new NotFoundException("Мероприятие не существует");
        Event event = eventRepository.getReferenceById(eventId);
        if (!event.getState().equals(State.PENDING))
            throw new StateException("Отменить событие можно только в статусе модерации");
        event.setState(State.CANCELED);
        return EventMapper.toDto(eventRepository.save(event));
    }

    private void existAndNotPublishedEvent(Long eventId) throws StateException, NotFoundException {
        if (!eventRepository.existsById(eventId)) throw new NotFoundException("Мероприятия не существует");
        if (eventRepository.getReferenceById(eventId).getState().equals(State.PUBLISHED))
            throw new StateException("Уже опубликовано");
    }
}
