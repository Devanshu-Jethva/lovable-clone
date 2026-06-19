package com.devanshu.lovableclone.entity;

import com.devanshu.lovableclone.constant.ProjectRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.time.Instant;

@Data
@Entity
@Table(name = "project_member")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMember extends CommonModel {

    @Serial
    private static final long serialVersionUID = 7259382695086338430L;

    ProjectMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projects_id", nullable = false)
    Projects projects;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role", nullable = false)
    ProjectRole projectRole;

    @Column(name = "invited_at", nullable = false)
    Instant invitedAt;

    @Column(name = "accepted_at")
    Instant acceptedAt;
}
