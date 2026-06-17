package com.devanshu.lovableclone.entity;

import com.devanshu.lovableclone.constant.MessageRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Data
@Entity
@Table(name = "chat_session")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage extends CommonModel {

    @Serial
    private static final long serialVersionUID = -8102710705923657906L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "chat_session_id")
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
