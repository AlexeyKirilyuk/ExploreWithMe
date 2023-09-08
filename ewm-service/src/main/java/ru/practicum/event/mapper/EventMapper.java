package ru.practicum.event.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.event.dto.UpdateEventUserDto;
import ru.practicum.event.enumerated.State;
import ru.practicum.event.model.Event;

public interface EventMapper {

    Event updateEvent(Event eventForUpdate, Category categoryForUpdate, State eventStateForUpdate, UpdateEventUserDto updateEventUserDto);
}
