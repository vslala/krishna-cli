package org.main.chatbot.domainmodels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String id;
    private String text;
    private String actor;

    public Message(String text, String actor) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.actor = actor;
    }
}
