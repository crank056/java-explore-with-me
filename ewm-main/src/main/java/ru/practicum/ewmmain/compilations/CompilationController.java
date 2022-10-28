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
    public List<CompilationDto> getAllCompilationsPublic(@RequestParam(required = false) Boolean pinned,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return compilationService.getAllCompilationsPublic(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationByIdPublic(@PathVariable Long compId) throws NotFoundException {
        return compilationService.getCompilationByIdPublic(compId);
    }

    @PostMapping("/admin/compilations")
    public CompilationDto createCompilationByAdmin(@RequestBody NewCompilationDto compilationDto) {
        return compilationService.createCompilationByAdmin(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public boolean deleteCompilation(@PathVariable Long compId) throws NotFoundException {
        return compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId, @PathVariable Long eventId)
        throws NotFoundException {
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable long compId, @PathVariable long eventId) throws NotFoundException {
        compilationService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin")
    public void unPinCompilaton(@PathVariable long compId) throws NotFoundException {
        compilationService.unPinCompilation(compId);
    }

    @PatchMapping("/admin/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable long compId) throws NotFoundException {
        compilationService.pinCompilation(compId);
    }
}
