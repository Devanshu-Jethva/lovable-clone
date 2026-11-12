package com.devanshu.lovableclone.entity;

import java.io.Serial;
import java.util.List;

import com.devanshu.lovableclone.constant.MessageRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@Table(name = "chat_message")
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends CommonModel {

	@Serial
	private static final long serialVersionUID = -8102710705923657906L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "projects_id", referencedColumnName = "projects_id", nullable = false)
	@JoinColumn(name = "users_id", referencedColumnName = "users_id", nullable = false)
	private ChatSession chatSession;

	/**
	 * Null unless {@link MessageRole} is USER
	 */
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "token_used", nullable = false)
	private Integer tokenUsed;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_role", nullable = false)
	private MessageRole messageRole;

	/**
	 * empty unless {@link MessageRole} is ASSISTANT
	 */
	@OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy("sequenceOrder ASC")
	private List<ChatEvent> events;
}
