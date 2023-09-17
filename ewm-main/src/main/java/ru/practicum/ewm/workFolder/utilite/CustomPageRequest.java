package ru.practicum.ewm.workFolder.utilite;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    @Getter
    private final int from;

    private CustomPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    public static CustomPageRequest by(int from, int size, Sort sort) {
        return new CustomPageRequest(from, size, sort);
    }
}