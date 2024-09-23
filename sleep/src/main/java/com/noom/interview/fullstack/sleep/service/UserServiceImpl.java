package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.exception.ResourceNotFoundException;
import com.noom.interview.fullstack.sleep.model.dto.request.UserCreateRequest;
import com.noom.interview.fullstack.sleep.model.entity.User;
import com.noom.interview.fullstack.sleep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        return user;
    }

    @Override
    public User createUser(UserCreateRequest request) {
        User user = User
                .builder()
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .build();
        return userRepository.save(user);
    }
}
