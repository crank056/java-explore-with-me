package ru.practicum.ewmmain.events.model;

import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.categories.CategoryRepository;
import ru.practicum.ewmmain.categories.model.CategoryMapper;
import ru.practicum.ewmmain.events.repositories.LocationRepository;
import ru.practicum.ewmmain.exceptions.WrongTimeException;
import ru.practicum.ewmmain.users.UserRepository;
import ru.practicum.ewmmain.users.model.User;
import ru.practicum.ewmmain.users.model.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class EventMapper {

    public static EventShortDto toShort(Event event) {
        return new EventShortDto(
            event.getId(),
            event.getTitle(),
            event.getAnnotation(),
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
            event.getTitle(),
            event.getAnnotation(),
            CategoryMapper.toDto(event.getCategory()),
            event.getDescription(),
            event.getCreatedOn(),
            event.getEventDate(),
            event.getPublishedOn(),
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

    public static Event fromNewToEntity(Long userId, NewEventDto newEventDto,
                                        LocationRepository locationRepository,
                                        CategoryRepository categoryRepository,
                                        UserRepository userRepository) throws WrongTimeException {
        Event event = new Event();
        Location location = locationRepository.findByLatAndLon(
            newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        if (location == null) location = locationRepository.save(newEventDto.getLocation());
        User initiator = userRepository.getReferenceById(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(newEventDto.getEventDate(), formatter);
        if (date.isBefore(LocalDateTime.now().plusHours(2))) throw new WrongTimeException("Неверное имя");
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(categoryRepository.getReferenceById(newEventDto.getCategory()));
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(date);
        event.setInitiator(initiator);
        event.setLocation(location);
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setState(State.PENDING);
        event.setTitle(newEventDto.getTitle());
        event.setViews(0L);
        return event;
    }
}
