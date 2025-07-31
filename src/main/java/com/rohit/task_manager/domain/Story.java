package com.rohit.task_manager.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer storyPoints;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private Instant expectedStartDateTime;

    private Instant expectedEndDateTime;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> childTasks;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "priority_id", nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private boolean isDeleted = false;
}
