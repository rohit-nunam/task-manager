package com.rohit.task_manager.controller;

import com.rohit.task_manager.domain.Task;
import com.rohit.task_manager.dto.input.TaskRequestDto;
import com.rohit.task_manager.dto.input.UpdateTaskStatusRequest;
import com.rohit.task_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
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
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(dto));
    }

    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request.getStatusId()));
    }

    @GetMapping("/tasks/search")
    public ResponseEntity<Page<Task>> searchTasks(
            @RequestParam(required = false) UUID userId, @RequestParam(required = false) String firstName,
            @RequestParam(required = false) Instant expectedEnd, @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.searchTasks(userId, firstName, expectedEnd, status, pageable));
    }

    @GetMapping("/tasks/filter")
    public ResponseEntity<Page<Task>> filterTasks(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.filterTasks(userId, status, priority, pageable));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.softDeleteTask(id);
        return ResponseEntity.noContent().build();
    }


}
