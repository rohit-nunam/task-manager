package com.rohit.task_manager.service;

import com.rohit.task_manager.domain.*;
import com.rohit.task_manager.dto.input.StoryRequestDto;
import com.rohit.task_manager.dto.input.TaskRequestDto;
import com.rohit.task_manager.respository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    public Story createStory(StoryRequestDto dto) {
        validateInProgressTimestamps(dto.getStatusId(), dto.getExpectedStartDateTime(), dto.getExpectedEndDateTime());

        User user = getUser(dto.getAssignedToId());
        Status status = getStatus(dto.getStatusId());
        Priority priority = getPriority(dto.getPriorityId());

        Story story = Story.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .storyPoints(dto.getStoryPoints())
                .status(status)
                .priority(priority)
                .assignedTo(user)
                .createdAt(Instant.now())
                .expectedStartDateTime(dto.getExpectedStartDateTime())
                .expectedEndDateTime(dto.getExpectedEndDateTime())
                .isDeleted(false)
                .build();

        return storyRepository.save(story);
    }

    public Task createTask(TaskRequestDto dto) {
        validateInProgressTimestamps(dto.getStatusId(), dto.getExpectedStartDateTime(), dto.getExpectedEndDateTime());

        User user = getUser(dto.getAssignedToId());
        Status status = getStatus(dto.getStatusId());
        Priority priority = getPriority(dto.getPriorityId());
        Story story = (dto.getStoryId() != null) ? storyRepository.findById(dto.getStoryId()).orElseThrow() : null;

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

        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long taskId, Long statusId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Status status = getStatus(statusId);

        if ("IN_PROGRESS".equalsIgnoreCase(status.getName())) {
            if (task.getExpectedStartDateTime() == null || task.getExpectedEndDateTime() == null) {
                throw new IllegalArgumentException("Expected start/end date must be set when status is IN_PROGRESS.");
            }
        }

        task.setStatus(status);
        task.setUpdatedAt(Instant.now());
        return taskRepository.save(task);
    }

    public void validateInProgressTimestamps(Long statusId, Instant start, Instant end) {
        Status status = getStatus(statusId);
        if ("IN_PROGRESS".equalsIgnoreCase(status.getName())) {
            if (start == null || end == null) {
                throw new IllegalArgumentException("Expected start/end date are mandatory for IN_PROGRESS status.");
            }
        }
    }

    public User getUser(UUID id) {
        return (id == null) ? null : userRepository.findById(id).orElseThrow();
    }

    public Status getStatus(Long id) {
        return statusRepository.findById(id).orElseThrow();
    }

    public Priority getPriority(Long id) {
        return priorityRepository.findById(id).orElseThrow();
    }

    public Page<Task> searchTasks(UUID userId, String firstName, Instant expectedEnd, String status, Pageable pageable) {
        return taskRepository.searchTasks(userId, firstName, expectedEnd, status, pageable);
    }

    public Page<Task> filterTasks(UUID userId, List<String> status, List<String> priority, Pageable pageable) {
        return taskRepository.filterTasks(userId, status, priority, pageable);
    }

    public void softDeleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setDeleted(true);
        task.setUpdatedAt(Instant.now());
        taskRepository.save(task);
    }

}
