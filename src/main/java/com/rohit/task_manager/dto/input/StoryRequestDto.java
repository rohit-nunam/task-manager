package com.rohit.task_manager.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class StoryRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Story points are required")
    private Integer storyPoints;

    @NotNull(message = "Status ID is required")
    private Long statusId;

    @NotNull(message = "Priority ID is required")
    private Long priorityId;

    private UUID assignedToId;

    private Instant expectedStartDateTime;

    private Instant expectedEndDateTime;
}
