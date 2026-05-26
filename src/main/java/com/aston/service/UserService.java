package com.aston.service;

import com.aston.entity.User;

import java.util.List;
import java.util.Optional;

import com.aston.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Optional<User> existing = userRepository.findById(user.getId());
        if (existing.isEmpty()) {
            throw new RuntimeException("User with id " + user.getId() + " not found");
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("User with id " + id + " not found");
        }

        userRepository.deleteById(id);
    }
}