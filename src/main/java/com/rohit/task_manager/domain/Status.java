package com.rohit.task_manager.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
