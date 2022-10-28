package ru.practicum.ewmmain.compilations;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.compilations.model.Compilation;
import ru.practicum.ewmmain.compilations.model.CompilationDto;
import ru.practicum.ewmmain.compilations.model.CompilationMapper;
import ru.practicum.ewmmain.compilations.model.NewCompilationDto;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.repositories.EventRepository;
import ru.practicum.ewmmain.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationService(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public List<CompilationDto> getAllCompilationsPublic(Boolean pinned, int from, int size) {
        List<Compilation> compilationlist;
        Pageable page = PageRequest.of(from / size, size);
        if (pinned != null) {
            compilationlist = compilationRepository.findAllByPinned(pinned, page).getContent();
        } else {
            compilationlist = compilationRepository.findAll(page).getContent();
        }
        return compilationlist.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilationByIdPublic(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Компиляции не существует");
        }
        return CompilationMapper.toDto(compilationRepository.getReferenceById(id));
    }

    public CompilationDto createCompilationByAdmin(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        List<Event> compilationList = new ArrayList<>();
        newCompilationDto.getEvents()
                .forEach(eventId -> compilationList.add(eventRepository.getReferenceById(eventId)));
        compilation.setEventList(compilationList);
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    public Boolean deleteCompilation(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Компиляции не существует");
        }
        compilationRepository.deleteById(id);
        return !compilationRepository.existsById(id);
    }

    public void deleteEventFromCompilation(Long compId, Long eventId) throws NotFoundException {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Компиляции не существует");
        }
        Compilation compilation = compilationRepository.getReferenceById(compId);
        List<Event> eventList = compilation.getEventList();
        eventList = eventList.stream()
                .filter(event -> !event.getId().equals(eventId))
                .collect(Collectors.toList());
        compilation.setEventList(eventList);
        compilationRepository.save(compilation);
    }

    public void addEventToCompilation(Long compId, Long eventId) throws NotFoundException {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Компиляции не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Мероприятия не существует");
        }
        Compilation compilation = compilationRepository.getReferenceById(compId);
        List<Event> compilationList = compilation.getEventList();
        compilationList.add(eventRepository.getReferenceById(eventId));
        compilation.setEventList(compilationList);
        compilationRepository.save(compilation);
    }

    public void pinCompilation(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Компиляции не существует");
        }
        Compilation compilation = compilationRepository.getReferenceById(id);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    public void unPinCompilation(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Компиляции не существует");
        }
        Compilation compilation = compilationRepository.getReferenceById(id);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }
}
