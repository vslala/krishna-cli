package org.main.chatbot.repositories.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "messages", schema = "messages")
public class MessageEntity {
    @Id
    private String id;

    private String text;
    private String actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;
}
