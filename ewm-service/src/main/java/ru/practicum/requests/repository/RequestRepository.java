package ru.practicum.requests.repository;

import java.util.Collection;
import java.util.List;

import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query("select new ru.practicum.requests.dto.ParticipationRequestDto(r.created, r.event.id, r.id,"
            + " r.requester.id, r.status ) "
            + "from Request as r "
            + "where r.event.initiator.id = :initiatorId and r.event.id = :eventId")
    List<ParticipationRequestDto> findAllRequestForEvent(@Param("initiatorId") Long initiatorId,
                                                         @Param("eventId") Long eventId);

    List<Request> findAllByIdInAndEventInitiatorIdAndEventId(Collection<Long> ids, Long userId, Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);
}