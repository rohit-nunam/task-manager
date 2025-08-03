package com.rohit.task_manager.controller;

import com.rohit.task_manager.dto.input.UserCreateRequest;
import com.rohit.task_manager.dto.output.UserDto;
import com.rohit.task_manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Log4j2
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user in the system with the provided information. " +
                    "The user ID is automatically generated as a UUID. "
                    + "This API returns the created user's details."
    )
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("Received request to create user: {}", request.getEmail());
        UserDto createdUser = userService.createUser(request);
        log.info("User created successfully with ID: {}", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    @Operation(
            summary = "Fetch user by ID",
            description = "Retrieves the details of a specific user using their UUID. "
                    + "Returns the user data if the user exists and is not soft-deleted."
    )
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        log.info("Fetching user with ID: {}", id);
        UserDto user = userService.getUserById(id);
        log.info("Fetched user: {}", user.getEmail());
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Soft delete a user",
            description = "Performs a soft delete on the user by marking them as inactive. "
                    + "The user is not permanently removed from the database."
    )
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting (soft) user with ID: {}", id);
        userService.softDeleteUser(id);
        log.info("User soft deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}
