package com.devanshu.lovableclone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Data
@Entity
@Table(name = "project_file")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFile extends CommonModel {

    @Serial
    private static final long serialVersionUID = 6942206736674639103L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projects_id", nullable = false)
    Projects projects;

    @Column(name = "path", nullable = false)
    String path;

    @Column(name = "minio_object_key", nullable = false)
    String minioObjectKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    Users createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    Users updatedBy;
}
