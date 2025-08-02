package com.rohit.task_manager;

import com.rohit.task_manager.domain.User;
import com.rohit.task_manager.dto.input.UserCreateRequest;
import com.rohit.task_manager.dto.output.UserDto;
import com.rohit.task_manager.exception.BadRequestException;
import com.rohit.task_manager.respository.UserRepository;
import com.rohit.task_manager.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private AutoCloseable closeable;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .firstName("Rohit")
                .lastName("Sharma")
                .email("rohit@example.com")
                .timeZone("Asia/Kolkata")
                .isDeleted(false)
                .build();
    }

    @Test
    void createUser_successful() {
        UserCreateRequest request = UserCreateRequest.builder()
                .firstName("Rohit")
                .lastName("Sharma")
                .email("rohit@example.com")
                .timeZone("Asia/Kolkata")
                .build();

        when(userRepository.findByEmail("rohit@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("rohit@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_duplicateEmail_throwsBadRequestException() {
        UserCreateRequest request = UserCreateRequest.builder()
                .firstName("Rohit")
                .email("rohit@example.com")
                .build();

        when(userRepository.findByEmail("rohit@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_successful() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Rohit", result.getFirstName());
    }

    @Test
    void getUserById_notFound_throwsException() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void softDeleteUser_setsDeletedTrue() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.softDeleteUser(userId);

        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void softDeleteUser_notFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.softDeleteUser(userId));
    }
}
