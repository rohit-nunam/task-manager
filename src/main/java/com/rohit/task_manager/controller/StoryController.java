package com.rohit.task_manager.controller;

import com.rohit.task_manager.domain.Story;
import com.rohit.task_manager.dto.input.StoryRequestDto;
import com.rohit.task_manager.service.StoryService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Log4j2
public class StoryController {

    private final StoryService storyService;

    @Autowired
    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping("/stories")
    public ResponseEntity<Story> createStory(@RequestBody @Valid StoryRequestDto dto) {
        log.info("Creating a new story for user!");
        Story story = storyService.createStory(dto);
        log.debug("Created story: {}", story);
        return ResponseEntity.status(HttpStatus.CREATED).body(story);
    }

    @GetMapping("/stories/{userId}")
    public ResponseEntity<Page<Story>> getStoriesForUser(
            @PathVariable UUID userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching stories for userId: {} with pageSize: {}", userId, pageable.getPageSize());
        Page<Story> stories = storyService.getStoriesByUser(userId, pageable);
        log.debug("Fetched {} stories for userId {}", stories.getTotalElements(), userId);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/stories/active")
    public ResponseEntity<List<Story>> getActiveStories(@RequestParam String timeZone) {
        log.info("Fetching active stories for timezone: {}", timeZone);
        List<Story> stories = storyService.getActiveStories(timeZone);
        log.debug("Fetched {} active stories", stories.size());
        return ResponseEntity.ok(stories);
    }
}
