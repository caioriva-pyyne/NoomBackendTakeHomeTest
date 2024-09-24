package com.noom.interview.fullstack.sleep.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
