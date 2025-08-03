package com.rohit.task_manager.controller;

import com.rohit.task_manager.domain.Story;
import com.rohit.task_manager.dto.input.StoryRequestDto;
import com.rohit.task_manager.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Story Management", description = "APIs for managing stories")
public class StoryController {

    private final StoryService storyService;

    @Autowired
    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @Operation(
            summary = "Create a new story",
            description = "Creates a new story for a specific user. The story will be associated with the user specified in the request payload."
    )
    @PostMapping("/stories")
    public ResponseEntity<Story> createStory(@RequestBody @Valid StoryRequestDto dto) {
        log.info("Creating a new story for user!");
        Story story = storyService.createStory(dto);
        log.debug("Created story: {}", story);
        return ResponseEntity.status(HttpStatus.CREATED).body(story);
    }

    @Operation(
            summary = "Fetch stories for a user",
            description = "Retrieves paginated list of stories created by a given user. Results are sorted by creation timestamp in descending order by default."
    )
    @GetMapping("/stories/{userId}")
    public ResponseEntity<Page<Story>> getStoriesForUser(
            @PathVariable UUID userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching stories for userId: {} with pageSize: {}", userId, pageable.getPageSize());
        Page<Story> stories = storyService.getStoriesByUser(userId, pageable);
        log.debug("Fetched {} stories for userId {}", stories.getTotalElements(), userId);
        return ResponseEntity.ok(stories);
    }

    @Operation(
            summary = "Get active stories",
            description = "Fetches all currently active stories based on the provided time zone. Active stories are those within their display duration window."
    )
    @GetMapping("/stories/active")
    public ResponseEntity<List<Story>> getActiveStories(@RequestParam String timeZone) {
        log.info("Fetching active stories for timezone: {}", timeZone);
        List<Story> stories = storyService.getActiveStories(timeZone);
        log.debug("Fetched {} active stories", stories.size());
        return ResponseEntity.ok(stories);
    }
}
