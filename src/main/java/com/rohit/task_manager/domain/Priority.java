package com.rohit.task_manager.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "priority")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Priority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    public Priority(String name) {
        this.name = name;
    }

    public Priority(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}