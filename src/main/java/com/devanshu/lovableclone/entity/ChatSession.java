package com.devanshu.lovableclone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.time.Instant;

@Data
@Entity
@Table(name = "chat_session")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession extends CommonModel {

    @Serial
    private static final long serialVersionUID = -6253140643260841031L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projects_id", nullable = false)
    Projects projects;

    @Column(name = "terminated_at")
    Instant terminatedAt;
}
