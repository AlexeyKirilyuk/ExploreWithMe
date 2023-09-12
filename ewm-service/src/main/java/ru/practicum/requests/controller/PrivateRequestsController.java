package ru.practicum.requests.controller;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestsController {

    private final RequestService requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId,
                                                 HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByRequester(@PathVariable Long userId,
                                                            @PathVariable Long requestId,
                                                            HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return requestService.cancelRequestByRequester(userId, requestId);
    }

    @GetMapping()
    public List<ParticipationRequestDto> getUserRequest(@PathVariable Long userId,
                                                        HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return requestService.getUserRequests(userId);
    }
}