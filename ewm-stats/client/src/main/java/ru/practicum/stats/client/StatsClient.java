package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.StatsDtoRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatsClient {
    private final String app;
    private final DateTimeFormatter formatter;
    private final RestTemplate restTemplate;

    public StatsClient(@Value("${app.stats-server-url}") String serverUrl,
                       @Value("${app.name}") String app,
                       @Value("${app.format.date-time}") String format,
                       RestTemplateBuilder builder) {
        this.app = app;
        this.formatter = DateTimeFormatter.ofPattern(format);
        this.restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    private static ResponseEntity<Object> responseEntity(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return bodyBuilder.body(response.getBody());
        }
        return bodyBuilder.build();
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    public void add(@NotNull HttpServletRequest httpServletRequest) {
        StatsDtoRequest dto = new StatsDtoRequest(null,
                app,
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now());
        HttpEntity<StatsDtoRequest> requestHttpEntity = new HttpEntity<>(dto, headers());
        try {
            restTemplate.exchange("/hit", HttpMethod.POST, requestHttpEntity, Object.class);
        } catch (HttpStatusCodeException o) {
            throw new RuntimeException(String.format("StatusCode: %d, ResponseBody: %s", o.getStatusCode().value(), o.getResponseBodyAsString()));
        }
    }

    public ResponseEntity<Object> search(@NotNull LocalDateTime start,
                                                         @NotNull LocalDateTime end,
                                                         List<String> uris,
                                                         Boolean unique) {
        Map<String, Object> setting = Map.of("start", start.format(formatter),
                "end", end.format(formatter),
                "uris", uris != null ? String.join(",", uris) : "",
                "unique", unique != null ? unique : false);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers());
        String path = "/stats" + "?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<Object> responseEntity = restTemplate.exchange(path, HttpMethod.GET, httpEntity, Object.class, setting);
        return responseEntity(responseEntity);
    }
}