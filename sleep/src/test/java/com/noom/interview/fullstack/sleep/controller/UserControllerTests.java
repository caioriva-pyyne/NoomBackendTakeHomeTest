package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noom.interview.fullstack.sleep.model.entity.User;
import com.noom.interview.fullstack.sleep.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// In a real world scenario, tests validating invalid request payloads should also be added
// similar to what was done for SleepLogController. This was not done due to time constraint.
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;
    private final User user = createUser();
    private final ObjectMapper objectMapper = new ObjectMapper();;

    @Test
    public void createUser_withValidData_returns201() throws Exception {
        // Arrange
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("name", "Bob Johnson");
        jsonNode.put("email", "bob.johnson@email.com");
        jsonNode.put("age", 30);
        String requestBody = objectMapper.writeValueAsString(jsonNode);
        when(userService.createUser(any())).thenReturn(user);

        // Act and assert
        mvc.perform(post("/api/user")
                        .param("userId", user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("data.id").value(user.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.age").value(user.getAge()));
    }

    private User createUser() {
        return User
                .builder()
                .id(UUID.randomUUID())
                .name("Bob Johnson")
                .email("bob.johnson@email.com")
                .age(30)
                .build();
    }
}
