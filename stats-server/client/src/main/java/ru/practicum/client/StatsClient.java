package ru.practicum.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StatsClient {
    private static String STATS_URL = "http://localhost:9090";

    private final RestTemplate restTemplate;

    public StatsClient() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    public void saveHit(EndpointHitDto endpointHitDto) {
        final String url = STATS_URL + "/hit";
        restTemplate.postForEntity(url, endpointHitDto, Void.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        final String url = STATS_URL + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                Map.of("start", start, "end", end, "uris", uris, "unique", unique)
        );
        return response.getBody();
    }
}
