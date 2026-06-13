package com.aston.controller;

import com.aston.dto.UserDto;
import com.aston.entity.User;
import com.aston.service.ExternalService;
import com.aston.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;
    private final ExternalService externalService;

    public UserController(UserService userService, ExternalService externalService) {
        this.userService = userService;
        this.externalService = externalService;
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }

    private EntityModel<UserDto> toModel(UserDto dto) {
        EntityModel<UserDto> model = EntityModel.of(dto);

        Link selfLink = linkTo(methodOn(UserController.class).getUserById(dto.getId())).withSelfRel();
        Link usersLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");

        model.add(selfLink, usersLink);
        return model;
    }

    @Operation(summary = "Создать пользователя", description = "Создаёт нового пользователя и возвращает его с ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestBody UserDto userDto) {
        User created = userService.createUser(
                userDto.getName(),
                userDto.getEmail(),
                userDto.getAge()
        );
        UserDto resultDto = toDto(created);
        EntityModel<UserDto> model = toModel(resultDto);

        Link deleteLink = linkTo(methodOn(UserController.class).deleteUser(resultDto.getId())).withRel("delete");
        Link updateLink = linkTo(methodOn(UserController.class).updateUser(resultDto.getId(), userDto)).withRel("update");

        model.add(deleteLink, updateLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен")
    })
    @GetMapping
    public ResponseEntity<List<EntityModel<UserDto>>> getAllUsers() {
        List<EntityModel<UserDto>> users = userService.getAllUsers()
                .stream()
                .map(this::toDto)
                .map(this::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> getUserById(
            @Parameter(description = "ID пользователя") @PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        UserDto dto = toDto(user);
        EntityModel<UserDto> model = toModel(dto);
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные существующего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @Parameter(description = "ID пользователя") @PathVariable Long id,
            @RequestBody UserDto userDto) {
        User user = new User(userDto.getName(), userDto.getEmail(), userDto.getAge());
        user.setId(id);
        User updated = userService.updateUser(user);
        UserDto resultDto = toDto(updated);
        EntityModel<UserDto> model = toModel(resultDto);

        Link deleteLink = linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete");
        model.add(deleteLink);

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Тест Circuit Breaker", description = "Вызов внешнего API с защитой Circuit Breaker")
    @GetMapping("/test-circuit-breaker")
    public ResponseEntity<String> testCircuitBreaker() {
        String result = externalService.callExternalApi();
        return ResponseEntity.ok(result);
    }
}