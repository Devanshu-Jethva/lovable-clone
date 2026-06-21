package com.devanshu.lovableclone.entity;

import com.devanshu.lovableclone.constant.ProjectMemberStatus;
import com.devanshu.lovableclone.constant.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_member")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMember extends CommonModel {

    @Serial
    private static final long serialVersionUID = 7259382695086338430L;

    @EmbeddedId
    ProjectMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "users_id", nullable = false)
    Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "projects_id", nullable = false)
    Projects projects;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role", nullable = false)
    ProjectRole projectRole;

    @Column(name = "invited_at", nullable = false)
    Instant invitedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "accepted_at")
    ProjectMemberStatus status;
}
