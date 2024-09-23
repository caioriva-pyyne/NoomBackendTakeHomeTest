package com.noom.interview.fullstack.sleep.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class UserCreateRequest {
    @JsonProperty
    @NotBlank
    private String name;

    @JsonProperty
    @Email
    private String email;

    @JsonProperty
    @NotNull
    @Min(18)
    @Max(110)
    private int age;
}
