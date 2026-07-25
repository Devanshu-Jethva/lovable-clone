package com.devanshu.lovableclone.entity;

import java.io.Serial;

import com.devanshu.lovableclone.constant.MessageRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
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
@Table(name = "chat_message")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage extends CommonModel {

	@Serial
	private static final long serialVersionUID = -8102710705923657906L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "projects_id", referencedColumnName = "projects_id", nullable = false),
			@JoinColumn(name = "users_id", referencedColumnName = "users_id", nullable = false) })
	ChatSession chatSession;

	@Column(name = "content", nullable = false)
	String content;

	@Column(name = "tool_calls", nullable = false)
	String toolCalls; // JSON array of tool calls

	@Column(name = "token_used", nullable = false)
	Integer tokenUsed;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_role", nullable = false)
	MessageRole messageRole;
}
