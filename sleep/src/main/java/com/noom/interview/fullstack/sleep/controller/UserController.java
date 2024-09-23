package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.model.dto.request.UserCreateRequest;
import com.noom.interview.fullstack.sleep.model.dto.response.UserResponse;
import com.noom.interview.fullstack.sleep.model.entity.User;
import com.noom.interview.fullstack.sleep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Spring controller for user related requests.
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {this.userService = userService;}

    /**
     * Creates a user.
     *
     * @param request data to create the user
     * @return the created user
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserResponse createSleepLog(@RequestBody @Valid UserCreateRequest request) {
        User user = userService.createUser(request);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge()
        );
    }
}
