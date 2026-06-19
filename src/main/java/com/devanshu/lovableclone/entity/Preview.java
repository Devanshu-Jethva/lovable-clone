package com.devanshu.lovableclone.entity;

import com.devanshu.lovableclone.constant.PreviewStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.time.Instant;

@Data
@Entity
@Table(name = "preview")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview extends CommonModel {

    @Serial
    private static final long serialVersionUID = 3288748332619495494L;

    @ManyToOne
    @JoinColumn(name = "projects_id")
    Projects projects;

    @Column(name = "namespace", nullable = false)
    String namespace;

    @Column(name = "pod_name", nullable = false)
    String podName;

    @Column(name = "preview_url", nullable = false)
    String previewUrl;

    @Column(name = "started_at", nullable = false)
    Instant startedAt;

    @Column(name = "terminated_at")
    Instant terminatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PreviewStatus status;

}
