package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.model.dto.request.UserCreateRequest;
import com.noom.interview.fullstack.sleep.model.entity.User;

import java.util.UUID;

/**
 * Interface that offers operations for user.
 */
public interface UserService {
    /**
     * Gets a user.
     *
     * @param userId the user id
     * @return the  user
     */
    User getUser(UUID userId);

    /**
     * Creates a user.
     *
     * @param request data to create the user
     * @return the created user
     */
    User createUser(UserCreateRequest request);
}
