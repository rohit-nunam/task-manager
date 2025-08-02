package com.rohit.task_manager.controller;

import com.rohit.task_manager.dto.input.UserCreateRequest;
import com.rohit.task_manager.dto.output.UserDto;
import com.rohit.task_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Log4j2
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("Received request to create user: {}", request.getEmail());
        UserDto createdUser = userService.createUser(request);
        log.info("User created successfully with ID: {}", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        log.info("Fetching user with ID: {}", id);
        UserDto user = userService.getUserById(id);
        log.info("Fetched user: {}", user.getEmail());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting (soft) user with ID: {}", id);
        userService.softDeleteUser(id);
        log.info("User soft deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}
