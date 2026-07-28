package com.devanshu.lovableclone.entity;

import java.io.Serial;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@Table(name = "chat_session")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession extends CommonModel {

	@Serial
	private static final long serialVersionUID = -6253140643260841031L;

	@EmbeddedId
	ChatSessionId id;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id", nullable = false)
	Users users;

	@MapsId("projectsId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "projects_id", nullable = false)
	Projects projects;

	@Column(name = "terminated_at")
	Instant terminatedAt;
}
