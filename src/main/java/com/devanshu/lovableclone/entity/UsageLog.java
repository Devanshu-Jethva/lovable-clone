package com.devanshu.lovableclone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Data
@Entity
@Table(name = "usage_log")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog extends CommonModel {

    @Serial
    private static final long serialVersionUID = 8611822990292863030L;

    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    Project project;

    @Column(name = "action", nullable = false)
    String action;

    @Column(name = "token_used", nullable = false)
    Integer tokenUsed;

    @Column(name = "duration_ms", nullable = false)
    Integer durationMs;

    @Column(name = "meta_data", columnDefinition = "TEXT")
    String metaData; // JSON of model used, prompt details, etc.
}
