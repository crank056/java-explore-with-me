package ru.practicum.ewmmain.compilations;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.compilations.model.CompilationDto;
import ru.practicum.ewmmain.compilations.model.NewCompilationDto;
import ru.practicum.ewmmain.exceptions.NotFoundException;

import java.util.List;

@RestController
@RequestMapping
public class CompilationController {

    private final CompilationService compilationService;

    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAllPublic(@RequestParam(required = false) Boolean pinned,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return compilationService.getAllPublic(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getByIdPublic(@PathVariable Long compId) throws NotFoundException {
        return compilationService.getByIdPublic(compId);
    }

    @PostMapping("/admin/compilations")
    public CompilationDto createByAdmin(@RequestBody NewCompilationDto compilationDto) {
        return compilationService.createByAdmin(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public boolean deleteCompilation(@PathVariable Long compId) throws NotFoundException {
        return compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public boolean deleteEventFromCompilation(@PathVariable Long compId, @PathVariable Long eventId)
        throws NotFoundException {
        return compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable long compId, @PathVariable long eventId) throws NotFoundException {
        compilationService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin")
    public void unPin(@PathVariable long compId) throws NotFoundException {
        compilationService.unPin(compId);
    }

    @PatchMapping("/admin/compilations/{compId}/pin")
    public void pin(@PathVariable long compId) throws NotFoundException {
        compilationService.pin(compId);
    }
}
