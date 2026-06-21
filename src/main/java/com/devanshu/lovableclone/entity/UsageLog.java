package com.devanshu.lovableclone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Getter
@Setter
@Entity
@Table(name = "usage_log")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog extends CommonModel {

    @Serial
    private static final long serialVersionUID = 8611822990292863030L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projects_id")
    Projects projects;

    @Column(name = "action", nullable = false)
    String action;

    @Column(name = "token_used", nullable = false)
    Integer tokenUsed;

    @Column(name = "duration_ms", nullable = false)
    Integer durationMs;

    @Column(name = "meta_data", columnDefinition = "TEXT")
    String metaData; // JSON of model used, prompt details, etc.
}
