package ru.practicum.ewmmain.events;

import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.events.model.EventMapper;
import ru.practicum.ewmmain.events.model.EventShortDto;
import ru.practicum.ewmmain.events.repositories.EventRepository;
import ru.practicum.ewmmain.events.repositories.LocationRepository;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    public List<EventShortDto> getAllPublic(String text, Long[] categories,
                                            Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable,
                                            String sort, int from, int size) {
        return null;
    }

    public EventShortDto getFromIdPublic(Long id) {
        //добавить проверку на опубликованность
        //добавить колвичество завпросов
        //добавить сохранение статистики
        return EventMapper.toShort(eventRepository.getReferenceById(id));
    }
}
