package com.aston.service;

import com.aston.dao.UserDao;
import com.aston.entity.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Invalid age");
        }

        User user = new User(name, email, age);
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Optional<User> existing = userDao.findById(user.getId());
        if (existing.isEmpty()) {
            throw new RuntimeException("User with id " + user.getId() + " not found");
        }

        userDao.update(user);
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<User> existing = userDao.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("User with id " + id + " not found");
        }

        userDao.delete(id);
    }
}
