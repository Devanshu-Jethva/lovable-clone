package com.devanshu.lovableclone.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class CommonModel implements Serializable {

	@Serial
	private static final long serialVersionUID = -7814065549397483516L;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	Instant updatedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	Long createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", nullable = false)
	Long updatedBy;
}
