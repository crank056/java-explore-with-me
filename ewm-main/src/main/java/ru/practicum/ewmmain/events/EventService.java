package ru.practicum.ewmmain.events;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.categories.CategoryRepository;
import ru.practicum.ewmmain.events.model.*;
import ru.practicum.ewmmain.events.repositories.EventRepository;
import ru.practicum.ewmmain.events.repositories.LocationRepository;
import ru.practicum.ewmmain.exceptions.NotFoundException;
import ru.practicum.ewmmain.exceptions.StateException;
import ru.practicum.ewmmain.exceptions.ValidationException;
import ru.practicum.ewmmain.users.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository,
                        LocationRepository locationRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    public List<EventShortDto> getAllPublic(String text, Long[] categories,
                                            Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable,
                                            String sort, int from, int size) {
        return null;
    }

    public EventShortDto getFromIdPublic(Long id) throws NotFoundException, StateException {
        if(!eventRepository.existsById(id)) throw new NotFoundException("Не найдено");
        if(!eventRepository.getReferenceById(id).getPublished().equals(State.PUBLISHED))
            throw new StateException("Не опубликовано");
        //добавить колвичество завпросов
        //добавить сохранение статистики
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
            for(Event event: list) {
                if(!usersList.contains(event.getInitiator().getId())) list.remove(event);
            }
        }
        if (states != null) {
            List<String> statesList = Arrays.asList(states);
            for(Event event: list) {
                if(!statesList.contains(event.getState().toString())) list.remove(event);
            }
        }
        if (categories != null) {
            List<Long> categoriesList = Arrays.asList(categories);
            for (Event event: list) {
                if(!categoriesList.contains(event.getCategory().getId())) list.remove(event);
            }
        }
        List<EventDto> eventDtoList = new ArrayList<>();
        for(Event event: list) {
            eventDtoList.add(EventMapper.toDto(event));
        }
        return eventDtoList;
    }

    public List<EventShortDto> getAllFromUser(Long userId, int from, int size) throws NotFoundException {
        if(!userRepository.existsById(userId)) throw new NotFoundException("Пользователь несуществует");
        Pageable page = PageRequest.of(from / size, size);
        List<Event> list = eventRepository.findAllByInitiatorId(userId, page).getContent();
        List<EventShortDto> dtoList = new ArrayList<>();
        for (Event event: list) {
            dtoList.add(EventMapper.toShort(event));
        }
        return dtoList;
    }

    public EventDto refreshFromUser(Long userId, UpdateEventRequest)

    private void existAndNotPublishedEvent(Long eventId) throws StateException, NotFoundException {
        if (!eventRepository.existsById(eventId)) throw new NotFoundException("Мероприятия не существует");
        if (eventRepository.getReferenceById(eventId).getState().equals(State.PUBLISHED))
            throw new StateException("Уже опубликовано");
    }
}
