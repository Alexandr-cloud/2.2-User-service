package com.aston.dao;

import com.aston.dao.impl.UserDaoImpl;
import com.aston.entity.User;
import com.aston.util.HibernateTestUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DisplayName("UserDaoImpl Integration Tests (PostgreSQL + Testcontainers)")
class UserDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        postgres.start();

        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        sessionFactory = HibernateTestUtil.getSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Nested
    @DisplayName("save() - сохранение пользователя")
    class SaveTests {

        @Test
        @DisplayName("Сохраняет пользователя и генерирует ID")
        void save_ShouldPersistUserAndGenerateId() {
            User user = new User("Иван", "ivan@mail.com", 25);

            User savedUser = userDao.save(user);

            assertNotNull(savedUser.getId());
            assertEquals("Иван", savedUser.getName());
            assertEquals("ivan@mail.com", savedUser.getEmail());
            assertEquals(25, savedUser.getAge());
            assertNotNull(savedUser.getCreatedAt());
        }

        @Test
        @DisplayName("Сохраняет пользователя с null age")
        void save_WithNullAge_ShouldPersist() {
            User user = new User("Иван", "ivan@mail.com", null);

            User savedUser = userDao.save(user);

            assertNotNull(savedUser.getId());
            assertNull(savedUser.getAge());
        }
    }

    @Nested
    @DisplayName("findById() - поиск по ID")
    class FindByIdTests {

        @Test
        @DisplayName("Находит пользователя по ID")
        void findById_WhenExists_ShouldReturnUser() {
            User user = new User("Иван", "ivan@mail.com", 25);
            User savedUser = userDao.save(user);

            Optional<User> found = userDao.findById(savedUser.getId());

            assertTrue(found.isPresent());
            assertEquals(savedUser.getId(), found.get().getId());
            assertEquals("Иван", found.get().getName());
        }

        @Test
        @DisplayName("Возвращает empty, когда пользователь не найден")
        void findById_WhenNotExists_ShouldReturnEmpty() {
            Optional<User> found = userDao.findById(999L);

            assertTrue(found.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAll() - получение всех пользователей")
    class FindAllTests {

        @Test
        @DisplayName("Возвращает всех пользователей")
        void findAll_ShouldReturnAllUsers() {
            userDao.save(new User("Иван", "ivan@mail.com", 25));
            userDao.save(new User("Мария", "maria@mail.com", 30));

            List<User> users = userDao.findAll();

            assertEquals(2, users.size());
        }

        @Test
        @DisplayName("Возвращает пустой список, когда нет пользователей")
        void findAll_WhenNoUsers_ShouldReturnEmptyList() {
            List<User> users = userDao.findAll();

            assertTrue(users.isEmpty());
        }
    }

    @Nested
    @DisplayName("update() - обновление пользователя")
    class UpdateTests {

        @Test
        @DisplayName("Обновляет существующего пользователя")
        void update_ShouldModifyUser() {
            User user = new User("Иван", "ivan@mail.com", 25);
            User saved = userDao.save(user);

            saved.setName("Иван Петрович");
            saved.setEmail("ivan.petrovich@mail.com");
            saved.setAge(26);
            userDao.update(saved);

            Optional<User> updated = userDao.findById(saved.getId());
            assertTrue(updated.isPresent());
            assertEquals("Иван Петрович", updated.get().getName());
            assertEquals("ivan.petrovich@mail.com", updated.get().getEmail());
            assertEquals(26, updated.get().getAge());
        }
    }

    @Nested
    @DisplayName("delete() - удаление пользователя")
    class DeleteTests {

        @Test
        @DisplayName("Удаляет существующего пользователя")
        void delete_ShouldRemoveUser() {
            User user = new User("Иван", "ivan@mail.com", 25);
            User saved = userDao.save(user);

            userDao.delete(saved.getId());

            Optional<User> found = userDao.findById(saved.getId());
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Не падает при удалении несуществующего пользователя")
        void delete_WhenNotExists_ShouldDoNothing() {
            assertDoesNotThrow(() -> userDao.delete(999L));
        }
    }
}