package com.rohit.task_manager.service;

import com.rohit.task_manager.domain.*;
import com.rohit.task_manager.dto.input.StoryRequestDto;
import com.rohit.task_manager.dto.input.TaskRequestDto;
import com.rohit.task_manager.respository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Log4j2
@Service
public class TaskService {

    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final PriorityRepository priorityRepository;

    @Autowired
    public TaskService(StoryRepository storyRepository, TaskRepository taskRepository, UserRepository userRepo,
                       StatusRepository statusRepo, PriorityRepository priorityRepository) {
        this.storyRepository = storyRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepo;
        this.statusRepository = statusRepo;
        this.priorityRepository = priorityRepository;
    }

    public Task createTask(TaskRequestDto dto) {
        log.info("Creating task: {}", dto.getTitle());
        validateInProgressTimestamps(dto.getStatusId(), dto.getExpectedStartDateTime(), dto.getExpectedEndDateTime());

        User user = getUser(dto.getAssignedToId());
        Status status = getStatus(dto.getStatusId());
        Priority priority = getPriority(dto.getPriorityId());
        Story story = (dto.getStoryId() != null) ? storyRepository.findById(dto.getStoryId()).orElse(null) : null;

        Task task = Task.builder()
                .title(dto.getTitle())
                .estimatedHours(dto.getEstimatedHours())
                .assignedTo(user)
                .status(status)
                .priority(priority)
                .story(story)
                .createdAt(Instant.now())
                .expectedStartDateTime(dto.getExpectedStartDateTime())
                .expectedEndDateTime(dto.getExpectedEndDateTime())
                .isDeleted(false)
                .build();

        Task saved = taskRepository.save(task);
        log.info("Task created with ID: {}", saved.getId());
        return saved;
    }

    @Cacheable(value = "searchTasksCache", key = "{#userId, #firstName, #expectedEnd, #status, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
    public Page<Task> searchTasks(UUID userId, String firstName, Instant expectedEnd, String status, Pageable pageable) {
        log.info("Searching tasks from DB with filters: userId={}, firstName={}, expectedEnd={}, status={}",
                userId, firstName, expectedEnd, status);
        return taskRepository.searchTasks(userId, firstName, expectedEnd, status, pageable);
    }

    @Cacheable(value = "filterTasksCache", key = "{#userId, #status, #priority, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
    public Page<Task> filterTasks(UUID userId, String status, String priority, Pageable pageable) {
        log.info("Filtering tasks from DB with userId={}, status={}, priority={}", userId, status, priority);
        return taskRepository.filterTasks(userId, status, priority, pageable);
    }

    @CacheEvict(value = { "searchTasksCache", "filterTasksCache" }, allEntries = true)
    public void softDeleteTask(Long id) {
        log.info("Soft deleting task with ID={}", id);
        Task task = taskRepository.findById(id).orElseThrow();
        task.setDeleted(true);
        task.setUpdatedAt(Instant.now());
        taskRepository.save(task);
        log.info("Task with ID={} soft deleted and cache evicted", id);
    }

    public Task updateTaskStatus(Long taskId, Long statusId) {
        log.info("Updating status for taskId={}, newStatusId={}", taskId, statusId);
        Task task = taskRepository.findById(taskId).orElseThrow();
        Status status = getStatus(statusId);

        if ("IN_PROGRESS".equalsIgnoreCase(status.getName())) {
            if (task.getExpectedStartDateTime() == null || task.getExpectedEndDateTime() == null) {
                log.warn("Invalid IN_PROGRESS timestamps for taskId={}", taskId);
                throw new IllegalArgumentException("Expected start/end date must be set when status is IN_PROGRESS.");
            }
        }

        task.setStatus(status);
        task.setUpdatedAt(Instant.now());
        Task updated = taskRepository.save(task);
        log.info("Task status updated successfully for taskId={}", taskId);
        return updated;
    }

    public void validateInProgressTimestamps(Long statusId, Instant start, Instant end) {
        Status status = getStatus(statusId);
        if ("IN_PROGRESS".equalsIgnoreCase(status.getName())) {
            if (start == null || end == null) {
                log.error("Timestamps missing for IN_PROGRESS status (statusId={})", statusId);
                throw new IllegalArgumentException("Expected start/end date are mandatory for IN_PROGRESS status.");
            }
        }
    }

    public User getUser(UUID id) {
        if (id == null) return null;
        log.debug("Fetching user with ID: {}", id);
        return userRepository.findById(id).orElseThrow();
    }

    public Status getStatus(Long id) {
        log.debug("Fetching status with ID: {}", id);
        return statusRepository.findById(id).orElseThrow();
    }

    public Priority getPriority(Long id) {
        log.debug("Fetching priority with ID: {}", id);
        return priorityRepository.findById(id).orElseThrow();
    }

}
