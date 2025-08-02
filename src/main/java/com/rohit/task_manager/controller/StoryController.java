package com.rohit.task_manager.controller;

import com.rohit.task_manager.domain.Story;
import com.rohit.task_manager.dto.input.StoryRequestDto;
import com.rohit.task_manager.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StoryController {

    private final StoryService storyService;

    @Autowired
    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping("/stories")
    public ResponseEntity<Story> createStory(@RequestBody @Valid StoryRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storyService.createStory(dto));
    }

    @GetMapping("/stories/{userId}")
    public ResponseEntity<Page<Story>> getStoriesForUser(
            @PathVariable UUID userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(storyService.getStoriesByUser(userId, pageable));
    }

    @GetMapping("/stories/active")
    public ResponseEntity<List<Story>> getActiveStories(@RequestParam String timeZone) {
        List<Story> stories = storyService.getActiveStories(timeZone);
        return ResponseEntity.ok(stories);
    }

}
