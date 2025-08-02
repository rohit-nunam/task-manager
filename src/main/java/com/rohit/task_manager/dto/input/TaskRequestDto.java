package com.rohit.task_manager.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TaskRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Title is required")
    private BigDecimal estimatedHours;

    @NotNull(message = "Title is required")
    private Long statusId;

    @NotNull(message = "priorityId is required")
    private Long priorityId;

    private UUID assignedToId;

    @NotNull(message = "storyId is required")
    private Long storyId;

    private Instant expectedStartDateTime;

    private Instant expectedEndDateTime;

}
