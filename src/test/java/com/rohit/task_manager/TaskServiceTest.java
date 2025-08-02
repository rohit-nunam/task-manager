package com.rohit.task_manager;

import com.rohit.task_manager.domain.Priority;
import com.rohit.task_manager.domain.Status;
import com.rohit.task_manager.domain.Task;
import com.rohit.task_manager.domain.User;
import com.rohit.task_manager.dto.input.TaskRequestDto;
import com.rohit.task_manager.respository.*;
import com.rohit.task_manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private StoryRepository storyRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private StatusRepository statusRepository;
    @Mock private PriorityRepository priorityRepository;

    @InjectMocks
    private TaskService taskService;

    private final UUID userId = UUID.randomUUID();
    private final Long statusId = 1L;
    private final Long priorityId = 2L;

    @Test
    void createTask_shouldSaveValidTask() {
        // Given
        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Test Task")
                .assignedToId(userId)
                .statusId(statusId)
                .priorityId(priorityId)
                .build();

        User user = User.builder().id(userId).firstName("John").build();
        Status status = new Status(statusId, "TODO");
        Priority priority = new Priority(priorityId, "HIGH");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(status));
        when(priorityRepository.findById(priorityId)).thenReturn(Optional.of(priority));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Task result = taskService.createTask(dto);

        // Then
        assertEquals("Test Task", result.getTitle());
        assertEquals(user, result.getAssignedTo());
        assertEquals(status, result.getStatus());
        assertEquals(priority, result.getPriority());
    }

    @Test
    void updateTaskStatus_shouldUpdateStatusAndSetUpdatedAt() {
        Task task = Task.builder().id(1L).expectedStartDateTime(Instant.now()).expectedEndDateTime(Instant.now()).build();
        Status newStatus = new Status(statusId, "IN_PROGRESS");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(newStatus));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        Task result = taskService.updateTaskStatus(1L, statusId);

        assertEquals("IN_PROGRESS", result.getStatus().getName());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void searchTasks_shouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(taskRepository.searchTasks(null, null, null, null, pageable))
                .thenReturn(Page.empty());

        Page<Task> result = taskService.searchTasks(null, null, null, null, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    void softDeleteTask_shouldSetDeletedTrueAndEvictCache() {
        Task task = Task.builder().id(1L).isDeleted(false).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        taskService.softDeleteTask(1L);
        assertTrue(task.isDeleted());
        assertNotNull(task.getUpdatedAt());
    }
}
