package ru.practicum.event.controller;

import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.event.dto.EventOutDto;
import ru.practicum.event.dto.UpdateEventUserDto;
import ru.practicum.event.enumerated.State;
import ru.practicum.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventOutDto publishOrCancelEvent(@PathVariable Long eventId,
                                            @RequestBody @Valid UpdateEventUserDto updateEventUserDto,
                                            HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return eventService.publishOrCancelEvent(eventId, updateEventUserDto);
    }

    @GetMapping
    public List<EventOutDto> findEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<State> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return eventService.findEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
