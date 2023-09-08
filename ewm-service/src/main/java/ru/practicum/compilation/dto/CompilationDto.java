package ru.practicum.compilation.dto;

import ru.practicum.event.dto.EventShortDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CompilationDto {

    private final Long id;

    @Setter
    private List<EventShortDto> events;

    private final boolean pinned;

    private final String title;
}
