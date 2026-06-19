package com.devanshu.lovableclone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.time.Instant;

@Data
@Entity
@Table(name = "projects")
@Builder
@EqualsAndHashCode(callSuper = true)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    Users owners;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    Boolean isPublic = false;

    @Column(name = "deleted_at")
    Instant deletedAt;
}
