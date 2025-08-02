package com.rohit.task_manager.service;

import com.rohit.task_manager.domain.User;
import com.rohit.task_manager.dto.input.UserCreateRequest;
import com.rohit.task_manager.dto.output.UserDto;
import com.rohit.task_manager.exception.BadRequestException;
import com.rohit.task_manager.respository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserCreateRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            log.warn("User with email {} already exists", request.getEmail());
            throw new BadRequestException("Email already exists");
        });

        User user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .timeZone(request.getTimeZone())
                .isDeleted(false)
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with ID: {}", saved.getId());
        return mapToDto(saved);
    }

    public UserDto getUserById(UUID id) {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.getUserById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found or deleted", id);
                    return new EntityNotFoundException("User not found or deleted!");
                });

        log.info("User with ID {} fetched successfully", id);
        return mapToDto(user);
    }

    public void softDeleteUser(UUID id) {
        log.info("Soft deleting user with ID: {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User with ID {} not found", id);
            return new EntityNotFoundException("User not found!");
        });

        user.setDeleted(true);
        userRepository.save(user);
        log.info("User with ID {} marked as deleted", id);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .timeZone(user.getTimeZone())
                .build();
    }
}
