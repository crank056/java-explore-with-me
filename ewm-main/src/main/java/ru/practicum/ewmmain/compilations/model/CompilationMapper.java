package ru.practicum.ewmmain.compilations.model;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTittle()
        );
    }

    public static Compilation toEntity(CompilationDto compilationDto) {
        return new Compilation(
                compilationDto.getId(),
                compilationDto.getPinned(),
                compilationDto.getTittle()
        );
    }
}
