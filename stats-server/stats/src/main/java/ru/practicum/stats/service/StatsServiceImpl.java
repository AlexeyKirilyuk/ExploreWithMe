package ru.practicum.stats.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private HitRepository hitRepository;

    @Override
    @Transactional()
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.trace("Вызов метода saveHit ({})", endpointHitDto);
        Hit hit = Hit.builder()
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .app(endpointHitDto.getApp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
        hitRepository.save(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.trace("Вызов метода getStats start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        List<ViewStatsDto> stats = new ArrayList<>();

        if (unique) {
            if (uris == null) {
                stats = hitRepository.getAllUniqueStats(start, end);
            } else {
                stats = hitRepository.getUniqueStatsByUrisAndTimestamps(start, end, uris);
            }
        } else {
            if (uris == null) {
                stats = hitRepository.getAllStats(start, end);
            } else {
                stats = hitRepository.getStatsByUrisAndTimestamps(start, end, uris);
            }
        }
        return stats;
    }
}
