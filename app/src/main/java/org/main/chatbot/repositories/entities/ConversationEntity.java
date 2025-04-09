package org.main.chatbot.repositories.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Entity
@Table(name = "conversations", schema = "conversations")
public class ConversationEntity {
    @Id
    private String id;

    private String title;
    private String summary;
    private Date createdAt;
    private Date lastModified;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MessageEntity> messages;
}
