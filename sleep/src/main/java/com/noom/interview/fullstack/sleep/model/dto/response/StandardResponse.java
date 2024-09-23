package com.noom.interview.fullstack.sleep.model.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public class StandardResponse<T> {
    private HttpStatus status;
    private Instant timestamp;
    private T data;

    public StandardResponse(HttpStatus status, T data) {
        this.status = status;
        timestamp = Instant.now();
        this.data = data;
    }
}
