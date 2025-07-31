package com.rohit.task_manager.service;

import com.rohit.task_manager.domain.Priority;
import com.rohit.task_manager.domain.Status;
import com.rohit.task_manager.domain.Story;
import com.rohit.task_manager.domain.User;
import com.rohit.task_manager.dto.input.StoryRequestDto;
import com.rohit.task_manager.respository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final TaskService taskService;

    private final StoryRepository storyRepository;

    @Autowired
    public StoryService(TaskService taskService, StoryRepository storyRepository) {
        this.taskService = taskService;
        this.storyRepository = storyRepository;
    }

    public Story createStory(StoryRequestDto dto) {

        taskService.validateInProgressTimestamps(dto.getStatusId(), dto.getExpectedStartDateTime(), dto.getExpectedEndDateTime());

        User user = taskService.getUser(dto.getAssignedToId());
        Status status = taskService.getStatus(dto.getStatusId());
        Priority priority = taskService.getPriority(dto.getPriorityId());

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

    public Page<Story> getStoriesByUser(UUID userId, Pageable pageable) {
        return storyRepository.findByAssignedToIdAndIsDeletedFalse(userId, pageable);
    }

    public List<Story> getActiveStories(String timeZone) {
        List<Story> activeStories = storyRepository.findActiveStories();

        return activeStories.stream()
                .peek(story -> {
                    // Convert expectedStartDateTime from UTC to given timeZone
                    if (story.getExpectedStartDateTime() != null) {
                        ZonedDateTime converted = ZonedDateTime.ofInstant(
                                story.getExpectedStartDateTime(),
                                ZoneId.of("UTC")
                        ).withZoneSameInstant(ZoneId.of(timeZone));

                        story.setExpectedStartDateTime(converted.toInstant()); // or set a new ZonedDateTime field if needed
                    }

                    // Do the same for expectedEndDateTime or other time fields if needed
                })
                .collect(Collectors.toList());
    }

}

