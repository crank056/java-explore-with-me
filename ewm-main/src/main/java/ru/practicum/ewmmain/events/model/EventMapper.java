package ru.practicum.ewmmain.events.model;

import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.categories.CategoryRepository;
import ru.practicum.ewmmain.categories.model.CategoryMapper;
import ru.practicum.ewmmain.events.repositories.EventRepository;
import ru.practicum.ewmmain.events.repositories.LocationRepository;
import ru.practicum.ewmmain.exceptions.WrongTimeException;
import ru.practicum.ewmmain.users.UserRepository;
import ru.practicum.ewmmain.users.model.User;
import ru.practicum.ewmmain.users.model.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class EventMapper {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventMapper(UserRepository userRepository, CategoryRepository categoryRepository,
                       EventRepository eventRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    public static EventShortDto toShort(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getTittle(),
                event.getAnnotations(),
                CategoryMapper.toDto(event.getCategory()),
                event.getDescription(),
                event.getEventDate(),
                event.getPaid(),
                event.getConfirmedRequests(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getViews()
        );
    }

    public static EventDto toDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getTittle(),
                event.getAnnotations(),
                CategoryMapper.toDto(event.getCategory()),
                event.getDescription(),
                event.getCreated(),
                event.getEventDate(),
                event.getPublished(),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getConfirmedRequests(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getState(),
                event.getViews()
        );
    }

    public Event fromNewToEntity(Long userId, NewEventDto newEventDto) throws WrongTimeException {
        Event event = new Event();
        Location location = locationRepository.findByLatAndLon(
            newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        if (location == null) locationRepository.save(newEventDto.getLocation());
        User initiator = userRepository.getReferenceById(userId);
        LocalDateTime date = LocalDateTime.parse(newEventDto.getEventDate(), formatter);
        if (date.isBefore(LocalDateTime.now().plusHours(2))) throw new WrongTimeException("Неверное имя");
        event.setAnnotations(newEventDto.getAnnotation());
        event.setCategory(categoryRepository.getReferenceById(newEventDto.getCategory()));
        event.setConfirmedRequests(0);
        event.setCreated(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(date);
        event.setInitiator(initiator);
        event.setLocation(location);
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setState(State.PENDING);
        event.setTittle(newEventDto.getTittle());
        event.setViews(0L);
        return event;
    }


}
