package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController()
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public EventOutDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                   @PathVariable Long userId,
                                   HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        log.info("NewEventDto newEventDto {} ", newEventDto);
        return eventService.createEvent(newEventDto, userId);

    }

    @GetMapping
    public List<EventShortDto> getUsersEvents(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size,
                                              HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventOutDto getUserEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventOutDto patchEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                  @RequestBody @Valid UpdateEventUserDto updateEventUserDto,
                                  HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return eventService.patchEvent(userId, eventId, updateEventUserDto);
    }

    @GetMapping("{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestsStatus(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest,
                                                               HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return eventService.changeRequestsStatus(userId, eventId, statusUpdateRequest);
    }
}
