package ru.practicum.ewmmain.compilations.model;

import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.model.EventMapper;
import ru.practicum.ewmmain.events.model.EventShortDto;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation) {
        List<Event> list = compilation.getEventList();
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for(Event event: list) {
            eventShortDtoList.add(EventMapper.toShort(event));
        }
        return new CompilationDto(
            eventShortDtoList,
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTittle()
        );
    }
}
