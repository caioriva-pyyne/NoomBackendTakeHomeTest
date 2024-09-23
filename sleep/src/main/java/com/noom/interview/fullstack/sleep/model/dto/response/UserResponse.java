package com.noom.interview.fullstack.sleep.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserResponse {
    @JsonProperty
    private UUID id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String email;

    @JsonProperty
    private int age;
}
