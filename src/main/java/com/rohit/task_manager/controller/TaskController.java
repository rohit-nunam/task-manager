package com.rohit.task_manager.controller;

import com.rohit.task_manager.domain.Task;
import com.rohit.task_manager.dto.input.TaskRequestDto;
import com.rohit.task_manager.dto.input.UpdateTaskStatusRequest;
import com.rohit.task_manager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Log4j2
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Create a new task",
            description = "Creates a new task using the provided task details."
    )
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDto dto) {
        log.info("Received request to create task: {}", dto);
        Task createdTask = taskService.createTask(dto);
        log.info("Task created successfully with ID: {}", createdTask.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Operation(
            summary = "Update task status",
            description = "Updates the status of an existing task using the task ID and new status."
    )
    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody UpdateTaskStatusRequest request) {
        log.info("Updating task status for taskId={}, newStatusId={}", id, request.getStatusId());
        Task updatedTask = taskService.updateTaskStatus(id, request.getStatusId());
        log.info("Task status updated successfully for taskId={}", id);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(
            summary = "Search tasks by filters",
            description = "Search for tasks based on optional user ID, user first name, expected end date, and status."
    )
    @GetMapping("/tasks/search")
    public ResponseEntity<Page<Task>> searchTasks(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) Instant expectedEnd,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        log.info("Searching tasks with userId={}, firstName={}, expectedEnd={}, status={}",
                userId, firstName, expectedEnd, status);
        Page<Task> result = taskService.searchTasks(userId, firstName, expectedEnd, status, pageable);
        log.info("Found {} tasks matching search criteria", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Filter tasks",
            description = "Filter tasks using optional user ID, status, and priority."
    )
    @GetMapping("/tasks/filter")
    public ResponseEntity<Page<Task>> filterTasks(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            Pageable pageable) {
        log.info("Filtering tasks with userId={}, status={}, priority={}", userId, status, priority);
        Page<Task> result = taskService.filterTasks(userId, status, priority, pageable);
        log.info("Found {} tasks matching filter criteria", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Soft delete a task",
            description = "Marks a task as deleted (soft delete) based on its ID."
    )
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Received request to delete task with ID={}", id);
        taskService.softDeleteTask(id);
        log.info("Task with ID={} soft-deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
