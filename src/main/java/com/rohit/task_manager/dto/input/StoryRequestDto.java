package com.rohit.task_manager.dto.input;

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

    private String title;

    private String description;

    private Integer storyPoints;

    private Long statusId;

    private Long priorityId;

    private UUID assignedToId;

    private Instant expectedStartDateTime;

    private Instant expectedEndDateTime;

}
