package com.rohit.task_manager.dto.input;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TaskRequestDto {

    private String title;

    private BigDecimal estimatedHours;

    private Long statusId;

    private Long priorityId;

    private UUID assignedToId;

    private Long storyId;

    private Instant expectedStartDateTime;

    private Instant expectedEndDateTime;

}
