package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserRequests(Long userId);
}
