package ru.practicum.event.dto;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class EventSearchParamsAdminDto {
    Set<Long> users;
    Set<State> states;
    Set<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Integer from;
    Integer size;

    public EventSearchParamsAdminDto(Set<Long> users,
                                     Set<State> states,
                                     Set<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Integer from,
                                     Integer size) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.from = from;
        this.size = size;
    }
}
