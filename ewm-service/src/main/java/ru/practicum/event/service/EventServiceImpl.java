package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.enumerated.Sorting;
import ru.practicum.event.enumerated.State;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.EventMapstructMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.requests.status.Status;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventMapstructMapper eventMapstructMapper;
    private final EventMapper eventMapperImpl;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public EventOutDto createEvent(NewEventDto newEventDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id " + userId + " не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(" Категория с " + newEventDto.getCategory() + " не найдена"));
        if (newEventDto.getEventDate() != null) {
            dateTimeValidate(newEventDto.getEventDate());
        }
        Location eventLocation = locationRepository.save(newEventDto.getLocation());
        Event newEvent = Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(eventLocation)
                .paid(newEventDto.getPaid() != null && newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration())
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .confirmedRequests(0L)
                .views(0L)
                .build();
        newEvent = eventRepository.save(newEvent);
        log.info("Создано новое событие= " + newEvent.getTitle());
        return eventMapstructMapper.eventToEventOutDto(newEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        PageRequest pagination = PageRequest.of(from / size,
                size);
        List<Event> allInitiatorEvents = eventRepository.findAllByInitiatorId(userId, pagination);
        List<EventShortDto> userEventsShortDtoList = eventMapstructMapper.eventsToEventShortDtoList(allInitiatorEvents);
        log.info("Пользователь с id= " + userId + " получил список своих событий= " + userEventsShortDtoList);
        return userEventsShortDtoList;
    }

    @Override
    public EventOutDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Событие с id= " + eventId + " не найдено");
        }
        EventOutDto eventOutDto = eventMapstructMapper.eventToEventOutDto(event);
        log.info("Пользователь с id= " + userId + " получил своё событие с id= " + eventId);
        return eventOutDto;
    }

    @Transactional
    @Override
    public EventOutDto patchEvent(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto) {
        Event event = eventRepository.findEventByIdWithCategoryAndLocation(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (updateEventUserDto.getEventDate() != null) {
            dateTimeValidate(updateEventUserDto.getEventDate());
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не должно быть опубликовано");
        }
        if (!event.getState().equals(State.PENDING) && !event.getState().equals(State.CANCELED)) {
            throw new ConflictException(
                    "Редактировать можно только событие в статусе pending (рассмотрение) и в статусе (canceled) отменено");
        }
        Category categoryForUpdate;
        if (updateEventUserDto.getCategory() != null) {
            categoryForUpdate = categoryRepository.findById(updateEventUserDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id= " + updateEventUserDto.getCategory()));
        } else {
            categoryForUpdate = event.getCategory();
        }
        State eventStateForUpdate = Optional.ofNullable(updateEventUserDto.getStateAction())
                .filter(state -> state.equals(State.REJECT_EVENT) || state.equals(State.CANCEL_REVIEW))
                .map(state -> State.CANCELED)
                .orElse(State.PENDING);
        event = eventMapperImpl.updateEvent(event, categoryForUpdate, eventStateForUpdate, updateEventUserDto);
        event = eventRepository.save(event);
        EventOutDto eventOutDto = eventMapstructMapper.eventToEventOutDto(event);
        log.info(" событие с id= " + eventId + " обновлено= " + event);
        return eventOutDto;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        List<ParticipationRequestDto> eventParticipationRequests = requestRepository.findAllRequestForEvent(userId,
                eventId);
        log.info("Получен список запросов на участие в событии с id= " + eventId + "\n Список запросов= "
                + eventParticipationRequests);
        return eventParticipationRequests;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest) {
        List<Request> requests = requestRepository.findAllByIdInAndEventInitiatorIdAndEventId(
                        statusUpdateRequest.getRequestIds(), userId,
                        eventId).stream()
                .peek(request -> {
                    if (request.getStatus() == Status.CONFIRMED && statusUpdateRequest.getStatus() == Status.REJECTED) {
                        throw new ConflictException("Вы не можете отменить принятую заявку");
                    }
                    request.setStatus(statusUpdateRequest.getStatus());
                })
                .collect(Collectors.toList());
        List<ParticipationRequestDto> participationRequests = requests.stream()
                .peek(request -> request.setStatus(statusUpdateRequest.getStatus()))
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
        EventRequestStatusUpdateResult updatedRequestsStatuses;
        if (statusUpdateRequest.getStatus().equals(Status.CONFIRMED)) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
            if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new ConflictException(
                        "Вы не можете добавить новых участников." + "\n" + event.getConfirmedRequests() + " из "
                                + event.getParticipantLimit() + " запланированных");
            }
            event.setConfirmedRequests(event.getConfirmedRequests() + statusUpdateRequest.getRequestIds().size());
            eventRepository.save(event);
            requestRepository.saveAll(requests);
            updatedRequestsStatuses = new EventRequestStatusUpdateResult(participationRequests, null);
        } else {
            updatedRequestsStatuses = new EventRequestStatusUpdateResult(null, participationRequests);
            requestRepository.saveAll(requests);
        }
        return updatedRequestsStatuses;
    }

    @Transactional
    @Override
    public EventOutDto publishOrCancelEvent(Long eventId, UpdateEventUserDto updateEventUserDto) {
        LocalDateTime publicationDate = LocalDateTime.now();
        if (updateEventUserDto.getEventDate() != null && updateEventUserDto.getEventDate()
                .isBefore(publicationDate.plusHours(1))
        ) {
            throw new BadRequestException(
                    "дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        Event event = eventRepository.findEventByIdWithCategoryAndLocation(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (event.getState().equals(State.PUBLISHED) && updateEventUserDto.getStateAction()
                .equals(State.REJECT_EVENT)) {
            throw new ConflictException("событие можно отклонить, только если оно еще не опубликовано");
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new ConflictException(
                    "событие можно публиковать, только если оно в состоянии ожидания публикации (PENDING)");
        }
        if (updateEventUserDto.getStateAction() == null) {
            updateEventUserDto.setStateAction(event.getState());
        }
        if (updateEventUserDto.getStateAction().equals(State.REJECT_EVENT)) {
            updateEventUserDto.setStateAction(State.CANCELED);
        }
        if (updateEventUserDto.getStateAction().equals(State.PUBLISH_EVENT)) {
            updateEventUserDto.setStateAction(State.PUBLISHED);
        }
        Category categoryForUpdate = null;
        if (updateEventUserDto.getCategory() != null) {
            categoryForUpdate = categoryRepository.findById(updateEventUserDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id= " + updateEventUserDto.getCategory()));
        }
        event = eventMapperImpl.updateEvent(event, categoryForUpdate, updateEventUserDto.getStateAction(),
                updateEventUserDto);
        event.setPublishedOn(publicationDate);
        event = eventRepository.save(event);
        EventOutDto eventOutDto = eventMapstructMapper.eventToEventOutDto(event);
        log.info("Событии= " + event.getTitle() + " присвоен статус= " + event.getState());
        return eventOutDto;
    }

    @Override
    public List<EventOutDto> findEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest pagination = PageRequest.of(from / size,
                size);
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart == null || rangeEnd == null) {
            start = LocalDateTime.now();
            end = start.plusYears(1);
        } else {
            start = rangeStart;
            end = rangeEnd;
        }
        List<EventOutDto> eventsByEventParamAndPaginationParams = eventRepository
                .findEventsByEventParamAndPaginationParams(
                        users,
                        states, categories, start,
                        end, pagination).stream()
                .map(eventMapstructMapper::eventToEventOutDto)
                .collect(Collectors.toList());
        log.info("Получен список событий, подходящих под условия= " + eventsByEventParamAndPaginationParams);
        return eventsByEventParamAndPaginationParams;
    }

    @Transactional
    @Override
    public EventOutDto getEvent(Long eventId, String[] uris) {
        Event event = eventRepository.findEventByIdWithCategoryAndLocation(
                eventId).orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие с id= " + eventId + " не найдено");
        }
        long eventStats = getViews(LocalDateTime
                .of(1990, 1, 1, 1, 1), LocalDateTime.now(), uris, true);
        event.setViews(eventStats);
        event = eventRepository.save(event);
        EventOutDto eventOut = eventMapstructMapper.eventToEventOutDto(event);
        log.info("Событие с id= " + eventId + " просмотрено");
        return eventOut;
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, boolean onlyAvailable, Sorting sort, int from, int size) {
        PageRequest pageRequest;
        LocalDateTime start;
        LocalDateTime end;
        Sort sorting;
        if (rangeStart == null || rangeEnd == null) {
            start = LocalDateTime.now();
            end = start.plusYears(1);
        } else {
            start = rangeStart;
            end = rangeEnd;
        }
        if (sort == null || sort.equals(Sorting.EVENT_DATE)) {
            sorting = Sort.by("eventDate").descending();
        } else {
            sorting = Sort.by("views").descending();
        }
        pageRequest = PageRequest.of(from / size, size, sorting);
        List<Event> eventsList;
        if (onlyAvailable) {
            eventsList = eventRepository.getOnlyAvailableEvents(text,
                    categories, paid,
                    start, end,
                    pageRequest);
        } else {
            eventsList = eventRepository.getEvents(text,
                    categories, paid,
                    start, end,
                    pageRequest);
        }
        if (eventsList.isEmpty() || !eventRepository.existsByState(State.PUBLISHED)) {
            throw new BadRequestException("Подходящие события не найдены");
        }
        List<EventShortDto> shortEventDtos = eventMapstructMapper.eventsToEventShortDtoList(eventsList);
        log.info("События получены по публичному эндпоинту= " + eventsList);
        return shortEventDtos;
    }

    private long getViews(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        List<ViewStatsDto> eventStats = statsClient.getStats(start, end, uris, unique);
        return eventStats.get(0).getHits();
    }

    private void dateTimeValidate(LocalDateTime localDateTime) {
        if (localDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    "Вы не можете добавить событие, которое проходит раньше чем за два часа от текущей даты "
                            + localDateTime);
        }
    }
}
