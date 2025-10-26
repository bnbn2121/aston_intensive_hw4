package com.aston.homework.controller;

import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.service.UserService;
import com.aston.homework.service.UserServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserDtoIn createTestUserDtoIn() {
        return new UserDtoIn("test", "test@test.com", 25);
    }

    private UserDtoOut createTestUserDtoOut() {
        UserDtoOut userDtoOut = new UserDtoOut();
        userDtoOut.setId(1);
        userDtoOut.setName("test");
        userDtoOut.setEmail("test@test.com");
        userDtoOut.setAge(25);
        userDtoOut.setCreatedAt(LocalDateTime.now());
        return userDtoOut;
    }

    @Test
    void shouldGetUserByIdSuccess() throws Exception {
        // Given
        int id = 1;
        UserDtoOut userDtoOut = createTestUserDtoOut();
        when(userService.getUserById(id)).thenReturn(userDtoOut);

        // When & Then
        mockMvc.perform(get("/app/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoOut.getId()))
                .andExpect(jsonPath("$.name").value(userDtoOut.getName()))
                .andExpect(jsonPath("$.email").value(userDtoOut.getEmail()))
                .andExpect(jsonPath("$.age").value(userDtoOut.getAge()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldGetUserByIdWithException() throws Exception {
        // Given
        int id = 999;

        when(userService.getUserById(id)).thenThrow(new UserServiceException("user not found"));

        // When & Then
        mockMvc.perform(get("/app/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("user not found"));
    }

    @Test
    void shouldAddUserSuccess() throws Exception {
        // Given
        UserDtoIn userDtoIn = createTestUserDtoIn();
        UserDtoOut userDtoOut = createTestUserDtoOut();

        when(userService.addUser(any(UserDtoIn.class))).thenReturn(userDtoOut);

        // When & Then
        mockMvc.perform(post("/app")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoOut.getId()))
                .andExpect(jsonPath("$.name").value(userDtoOut.getName()))
                .andExpect(jsonPath("$.email").value(userDtoOut.getEmail()))
                .andExpect(jsonPath("$.age").value(userDtoOut.getAge()))
                .andExpect(jsonPath("$.createdAt").exists());
        verify(userService).addUser(any(UserDtoIn.class));
    }

    @Test
    void shouldAddUserWithException() throws Exception {
        // Given
        UserDtoIn userDtoIn = createTestUserDtoIn();

        when(userService.addUser(any(UserDtoIn.class))).thenThrow(new UserServiceException("saving user is unavailable, such email exists"));

        // When & Then
        mockMvc.perform(post("/app")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isConflict())
                .andExpect(content().string("saving user is unavailable, such email exists"));
        verify(userService).addUser(any(UserDtoIn.class));
    }

    @Test
    void shouldUpdateUserSuccess() throws Exception {
        // Given
        int id = 1;
        UserDtoIn userDtoIn = createTestUserDtoIn();
        UserDtoOut userDtoOut = createTestUserDtoOut();

        when(userService.updateUserById(eq(id), any(UserDtoIn.class))).thenReturn(userDtoOut);

        // When & Then
        mockMvc.perform(put("/app/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoOut.getId()))
                .andExpect(jsonPath("$.name").value(userDtoOut.getName()))
                .andExpect(jsonPath("$.email").value(userDtoOut.getEmail()))
                .andExpect(jsonPath("$.age").value(userDtoOut.getAge()))
                .andExpect(jsonPath("$.createdAt").exists());
        verify(userService).updateUserById(eq(id), any(UserDtoIn.class));
    }

    @Test
    void shouldUpdateUserWithException() throws Exception {
        // Given
        int id = 1;
        UserDtoIn userDtoIn = createTestUserDtoIn();

        when(userService.updateUserById(eq(id), any(UserDtoIn.class))).thenThrow(new UserServiceException("saving user is unavailable, such email exists"));

        // When & Then
        mockMvc.perform(put("/app/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isConflict())
                .andExpect(content().string("saving user is unavailable, such email exists"));
        verify(userService).updateUserById(eq(id), any(UserDtoIn.class));
    }

    @Test
    void shouldDeleteUserSuccess() throws Exception {
        // Given
        int id = 1;

        when(userService.deleteUser(id)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/app/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("success delete: true"));
        verify(userService).deleteUser(id);
    }

    @Test
    void shouldDeleteUserWithException() throws Exception {
        // Given
        int id = 1;

        when(userService.deleteUser(id)).thenThrow(new UserServiceException("error"));

        // When & Then
        mockMvc.perform(delete("/app/1"))
                .andExpect(status().isBadRequest());
        verify(userService).deleteUser(id);
    }
}
