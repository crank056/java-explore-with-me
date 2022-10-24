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

    public List<CompilationDto> getAllPublic(Boolean pinned, int from, int size) {
        List<Compilation> list;
        Pageable page = PageRequest.of(from / size, size);
        if (pinned != null) {
            list = compilationRepository.findAllByPinned(pinned, page).getContent();
        } else {
            list = compilationRepository.findAll(page).getContent();
        }
        return list.stream()
            .map(compilation -> CompilationMapper.toDto(compilation)).collect(Collectors.toList());
    }

    public CompilationDto getByIdPublic(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) throw new NotFoundException("Компиляции не существует");
        return CompilationMapper.toDto(compilationRepository.getReferenceById(id));
    }

    public CompilationDto createByAdmin(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        List<Event> list = new ArrayList<>();
        for (Long eventId : newCompilationDto.getEvents()) {
            list.add(eventRepository.getReferenceById(eventId));
        }
        compilation.setEventList(list);
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setTittle(newCompilationDto.getTitle());
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    public Boolean deleteCompilation(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) throw new NotFoundException("Компиляции не существует");
        compilationRepository.deleteById(id);
        return !compilationRepository.existsById(id);
    }

    public Boolean deleteEventFromCompilation(Long compId, Long eventId) throws NotFoundException {
        if (!compilationRepository.existsById(compId)) throw new NotFoundException("Компиляции не существует");
        Compilation compilation = compilationRepository.getReferenceById(compId);
        boolean isExist = false;
        List<Event> list = compilation.getEventList();
        for (Event event : list) {
            if (event.getId().equals(eventId)) {
                list.remove(event);
                isExist = true;
            }
        }
        if (!isExist) throw new NotFoundException("Такого мероприятия нет в подборке");
        compilation.setEventList(list);
        compilationRepository.save(compilation);
        return isExist;
    }

    public void addEventToCompilation(Long compId, Long eventId) throws NotFoundException {
        if (!compilationRepository.existsById(compId)) throw new NotFoundException("Компиляции не существует");
        if (!eventRepository.existsById(eventId)) throw new NotFoundException("Мероприятия не существует");
        Compilation compilation = compilationRepository.getReferenceById(compId);
        List<Event> list = compilation.getEventList();
        list.add(eventRepository.getReferenceById(eventId));
        compilation.setEventList(list);
        compilationRepository.save(compilation);
    }

    public void pin(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) throw new NotFoundException("Компиляции не существует");
        Compilation compilation = compilationRepository.getReferenceById(id);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    public void unPin(Long id) throws NotFoundException {
        if (!compilationRepository.existsById(id)) throw new NotFoundException("Компиляции не существует");
        Compilation compilation = compilationRepository.getReferenceById(id);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }
}
