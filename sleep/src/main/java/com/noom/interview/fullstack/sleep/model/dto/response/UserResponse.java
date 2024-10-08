package com.noom.interview.fullstack.sleep.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private int age;
}
