package com.aston.service;

import com.aston.entity.User;
import com.aston.event.UserEvent;
import com.aston.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer eventProducer;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("createUser() - позитивные сценарии")
    class CreateUserPositiveTests {

        @Test
        @DisplayName("Создаёт пользователя и возвращает его с ID")
        void createUser_ValidData_ShouldReturnSavedUser() {
            String name = "Иван";
            String email = "ivan@mail.com";
            Integer age = 25;

            User expectedUser = new User(name, email, age);
            expectedUser.setId(1L);

            when(userRepository.save(any(User.class))).thenReturn(expectedUser);
            doNothing().when(eventProducer).sendUserEvent(any(UserEvent.class));

            User result = userService.createUser(name, email, age);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(name, result.getName());
            assertEquals(email, result.getEmail());
            assertEquals(age, result.getAge());

            verify(userRepository, times(1)).save(any(User.class));
            verify(eventProducer, times(1)).sendUserEvent(any(UserEvent.class));
        }
    }

    @Nested
    @DisplayName("createUser() - негативные сценарии")
    class CreateUserNegativeTests {

        @Test
        @DisplayName("Бросает исключение, если имя пустое")
        void createUser_EmptyName_ShouldThrowException() {
            String name = "";
            String email = "ivan@mail.com";
            Integer age = 25;

            assertThrows(IllegalArgumentException.class, () ->
                    userService.createUser(name, email, age)
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Бросает исключение, если email пустой")
        void createUser_EmptyEmail_ShouldThrowException() {
            String name = "Иван";
            String email = "";
            Integer age = 25;

            assertThrows(IllegalArgumentException.class, () ->
                    userService.createUser(name, email, age)
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Бросает исключение, если возраст отрицательный")
        void createUser_NegativeAge_ShouldThrowException() {
            String name = "Иван";
            String email = "ivan@mail.com";
            Integer age = -5;

            assertThrows(IllegalArgumentException.class, () ->
                    userService.createUser(name, email, age)
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Бросает исключение, если возраст больше 150")
        void createUser_AgeOver150_ShouldThrowException() {
            String name = "Иван";
            String email = "ivan@mail.com";
            Integer age = 200;

            assertThrows(IllegalArgumentException.class, () ->
                    userService.createUser(name, email, age)
            );

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserByIdTests {

        @Test
        @DisplayName("Возвращает пользователя, когда он найден")
        void getUserById_WhenUserExists_ShouldReturnUser() {
            Long userId = 1L;
            User user = new User("Иван", "ivan@mail.com", 25);
            user.setId(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            Optional<User> result = userService.getUserById(userId);

            assertTrue(result.isPresent());
            assertEquals(userId, result.get().getId());
            assertEquals("Иван", result.get().getName());

            verify(userRepository, times(1)).findById(userId);
        }

        @Test
        @DisplayName("Возвращает пустой Optional, когда пользователь не найден")
        void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            Optional<User> result = userService.getUserById(userId);

            assertTrue(result.isEmpty());

            verify(userRepository, times(1)).findById(userId);
        }
    }

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {

        @Test
        @DisplayName("Возвращает список пользователей, когда они есть")
        void getAllUsers_WhenUsersExist_ShouldReturnList() {
            User user1 = new User("Иван", "ivan@mail.com", 25);
            user1.setId(1L);
            User user2 = new User("Мария", "maria@mail.com", 30);
            user2.setId(2L);
            List<User> users = Arrays.asList(user1, user2);

            when(userRepository.findAll()).thenReturn(users);
            List<User> result = userService.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Иван", result.get(0).getName());
            assertEquals("Мария", result.get(1).getName());

            verify(userRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Возвращает пустой список, когда пользователей нет")
        void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<User> result = userService.getAllUsers();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        @Test
        @DisplayName("Обновляет пользователя")
        void updateUser_ShouldCallDaoUpdate() {
            User user = new User("Иван", "ivan@mail.com", 25);
            user.setId(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.updateUser(user);

            verify(userRepository, times(1)).findById(1L);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Бросает исключение, если ID = null")
        void updateUser_NullId_ShouldThrowException() {
            User user = new User("Иван", "ivan@mail.com", 25);
            user.setId(null);

            assertThrows(IllegalArgumentException.class, () ->
                    userService.updateUser(user)
            );

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUserTests {

        @Test
        @DisplayName("Удаляет пользователя")
        void deleteUser_WhenExists_ShouldCallDaoDelete() {
            Long userId = 1L;
            User user = new User("Иван", "ivan@mail.com", 25);
            user.setId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            doNothing().when(userRepository).deleteById(userId);
            doNothing().when(eventProducer).sendUserEvent(any(UserEvent.class));

            userService.deleteUser(userId);

            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).deleteById(userId);
            verify(eventProducer, times(1)).sendUserEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Бросает исключение, если ID = null")
        void deleteUser_NullId_ShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.deleteUser(null)
            );

            verify(userRepository, never()).deleteById(any());
        }
    }
}