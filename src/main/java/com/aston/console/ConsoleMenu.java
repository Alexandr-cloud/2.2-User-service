package com.aston.console;

import com.aston.entity.User;
import com.aston.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleMenu(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Выберите действие: ");

            switch (choice) {
                case 1 -> createUser();
                case 2 -> findUserById();
                case 3 -> findAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> {
                    running = false;
                    System.out.println("До свидания!");
                }
                default -> System.out.println("Неверный выбор");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("Выберите действие от 1 до 6");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Выход");
    }

    private void createUser() {
        System.out.println("СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ");

        String name = readString("Введите имя: ");
        String email = readString("Введите email: ");
        Integer age = readInt("Введите возраст: ");

        try {
            User created = userService.createUser(name, email, age);
            System.out.println("Пользователь создан! ID: " + created.getId());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void findUserById() {
        System.out.println("ПОИСК ПОЛЬЗОВАТЕЛЯ");

        Long id = (long) readInt("Введите ID пользователя: ");

        Optional<User> userOpt = userService.getUserById(id);

        if (userOpt.isPresent()) {
            System.out.println("Найден пользователь:");
            System.out.println(userOpt.get());
        } else {
            System.out.println("Пользователь с ID " + id + " не найден");
        }
    }


    private void findAllUsers() {
        System.out.println("ВСЕ ПОЛЬЗОВАТЕЛИ");

        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Пользователи не найдены");
            } else {
                System.out.println("Найдено пользователей: " + users.size());
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void updateUser() {
        System.out.println("ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");

        Long id = (long) readInt("Введите ID пользователя для обновления: ");

        Optional<User> existingOpt = userService.getUserById(id);

        if (existingOpt.isEmpty()) {
            System.out.println("Пользователь с ID " + id + " не найден");
            return;
        }

        User existing = existingOpt.get();
        System.out.println("Текущие данные: " + existing);

        System.out.println("(Оставьте поле пустым, чтобы не менять)");

        String name = readStringWithDefault("Новое имя", existing.getName());
        String email = readStringWithDefault("Новый email", existing.getEmail());
        String ageStr = readStringWithDefault("Новый возраст", String.valueOf(existing.getAge()));
        Integer age = ageStr.isBlank() ? existing.getAge() : Integer.parseInt(ageStr);

        User updated = new User(name, email, age);
        updated.setId(id);

        userService.updateUser(updated);
        System.out.println("Пользователь обновлен!");
    }

    private void deleteUser() {
        System.out.println("УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");

        Long id = (long) readInt("Введите ID пользователя для удаления: ");

        try {
            userService.deleteUser(id);
            System.out.println("Пользователь удален!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Ошибка. Введите число: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine();
        return result;
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String readStringWithDefault(String prompt, String defaultValue) {
        System.out.print(prompt + " [" + defaultValue + "]: ");
        String input = scanner.nextLine();
        return input.isBlank() ? defaultValue : input;
    }
}
