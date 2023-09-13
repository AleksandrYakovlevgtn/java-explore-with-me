package ru.practicum.stats.server.exeption;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

@Value
public class ErrorResponse {
    @JsonProperty("reasons")
    List<String> reasons;
    @JsonProperty("status")
    int status;
    @JsonProperty("error")
    String error;
    @JsonProperty("offset-timestamp")
    OffsetDateTime timestamp = OffsetDateTime.now();

    @JsonProperty("end-point")
    String endPoint;
}