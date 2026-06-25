package com.devanshu.lovableclone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "projects",
        indexes = {
                @Index(name = "idx_projects_updated_at_desc_deleted_at", columnList = "updated_at DESC, deleted_at"),
                @Index(name = "idx_projects_deleted_at_updated_at_desc", columnList = "deleted_at, updated_at DESC"),
                @Index(name = "idx_projects_deleted_at", columnList = "deleted_at")
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Projects extends CommonModel {

    @Serial
    private static final long serialVersionUID = -6104905132933799694L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    Boolean isPublic = false;

    @Column(name = "deleted_at")
    Instant deletedAt;

    @OneToMany(mappedBy = "projects")
    private List<ProjectMember> projectMembers;
}
