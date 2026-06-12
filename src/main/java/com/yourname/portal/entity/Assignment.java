package com.yourname.portal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter  // <-- Use this instead of @Data
@Setter  // <-- Use this instead of @Data
@Table(name = "assignments")

public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to the Task entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // Links to the User entity (the staff member)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User assignedTo;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    private String adminNotes; // Optional instructions from the admin
}