package com.aston.controller;

import com.aston.dto.UserDto;
import com.aston.entity.User;
import com.aston.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController API Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users - создаёт пользователя")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDto requestDto = new UserDto();
        requestDto.setName("Иван");
        requestDto.setEmail("ivan@mail.com");
        requestDto.setAge(25);

        User savedUser = new User("Иван", "ivan@mail.com", 25);
        savedUser.setId(1L);

        when(userService.createUser(any(), any(), any())).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@mail.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService, times(1)).createUser(any(), any(), any());
    }

    @Test
    @DisplayName("GET /api/users - возвращает всех пользователей")
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        User user1 = new User("Иван", "ivan@mail.com", 25);
        user1.setId(1L);
        User user2 = new User("Мария", "maria@mail.com", 30);
        user2.setId(2L);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Мария"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/users/{id} - возвращает пользователя по ID")
    void getUserById_WhenExists_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        User user = new User("Иван", "ivan@mail.com", 25);
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("GET /api/users/{id} - возвращает 404 если пользователь не найден")
    void getUserById_WhenNotExists_ShouldReturn404() throws Exception {
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("PUT /api/users/{id} - обновляет пользователя")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Long userId = 1L;
        UserDto requestDto = new UserDto();
        requestDto.setName("Иван Петрович");
        requestDto.setEmail("ivan@mail.com");
        requestDto.setAge(26);

        User updatedUser = new User("Иван Петрович", "ivan@mail.com", 26);
        updatedUser.setId(userId);

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Петрович"))
                .andExpect(jsonPath("$.age").value(26));

        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - удаляет пользователя")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }
}