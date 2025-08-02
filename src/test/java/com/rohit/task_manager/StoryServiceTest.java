package com.rohit.task_manager;

import com.rohit.task_manager.domain.*;
import com.rohit.task_manager.dto.input.StoryRequestDto;
import com.rohit.task_manager.respository.StoryRepository;
import com.rohit.task_manager.service.StoryService;
import com.rohit.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoryServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private StoryRepository storyRepository;

    @InjectMocks
    private StoryService storyService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetActiveStories_withTimeZoneConversion() {
        // Given
        Instant utcStartTime = Instant.parse("2025-07-30T08:00:00Z");
        Story story = Story.builder()
                .expectedStartDateTime(utcStartTime)
                .status(new Status("IN_PROGRESS"))
                .priority(new Priority("LOW"))
                .isDeleted(false)
                .build();

        when(storyRepository.findActiveStories()).thenReturn(List.of(story));

        // When
        List<Story> stories = storyService.getActiveStories("Asia/Kolkata");

        // Then
        assertEquals(1, stories.size());

        Instant convertedInstant = stories.get(0).getExpectedStartDateTime();
        assertNotNull(convertedInstant);

        // The converted instant will still be the same UTC value
        assertEquals(utcStartTime, convertedInstant);

        // Verify the local time in Asia/Kolkata is correct
        ZonedDateTime localTime = convertedInstant.atZone(ZoneId.of("Asia/Kolkata"));
        assertEquals(13, localTime.getHour());
        assertEquals(30, localTime.getMinute());
    }


    @Test
    void testCreateStory_savesCorrectly() {
        // Given
        UUID userId = UUID.randomUUID();
        StoryRequestDto dto = StoryRequestDto.builder()
                .title("Test Story")
                .description("desc")
                .storyPoints(3)
                .assignedToId(userId)
                .statusId(1L)
                .priorityId(2L)
                .expectedStartDateTime(Instant.now())
                .expectedEndDateTime(Instant.now().plusSeconds(3600))
                .build();

        User mockUser = new User(userId);
        Status status = new Status("IN_PROGRESS");
        Priority priority = new Priority("MEDIUM");

        when(taskService.getUser(userId)).thenReturn(mockUser);
        when(taskService.getStatus(dto.getStatusId())).thenReturn(status);
        when(taskService.getPriority(dto.getPriorityId())).thenReturn(priority);

        Story savedStory = Story.builder()
                .id(1L)
                .title(dto.getTitle())
                .assignedTo(mockUser)
                .status(status)
                .priority(priority)
                .isDeleted(false)
                .build();

        when(storyRepository.save(any(Story.class))).thenReturn(savedStory);

        // When
        Story result = storyService.createStory(dto);

        // Then
        assertNotNull(result);
        assertEquals("Test Story", result.getTitle());
        assertEquals(mockUser, result.getAssignedTo());
        verify(storyRepository, times(1)).save(any(Story.class));
    }

    @Test
    void testGetStoriesByUser_returnsPagedResults() {
        UUID userId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 5);
        Story s1 = new Story();
        s1.setTitle("Story 1");

        when(storyRepository.findByAssignedToIdAndIsDeletedFalse(eq(userId), any()))
                .thenReturn(new PageImpl<>(List.of(s1)));

        Page<Story> result = storyService.getStoriesByUser(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Story 1", result.getContent().get(0).getTitle());
    }

}
