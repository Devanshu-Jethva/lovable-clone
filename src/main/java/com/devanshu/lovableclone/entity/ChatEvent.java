package com.devanshu.lovableclone.entity;

import java.io.Serial;

import com.devanshu.lovableclone.constant.ChatEventType;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "chat_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatEvent extends CommonModel {

	@Serial
	private static final long serialVersionUID = 5725277507822508993L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_message_id", nullable = false)
	ChatMessage chatMessage;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	ChatEventType type;

	@Column(name = "sequence_order", nullable = false)
	Integer sequenceOrder;

	@Column(name = "content", columnDefinition = "text")
	String content;

	@Column(name = "file_path")
	String filePath; // NULL unless FILE_EDIT

	@Column(name = "metadata", columnDefinition = "text")
	String metadata;

}
