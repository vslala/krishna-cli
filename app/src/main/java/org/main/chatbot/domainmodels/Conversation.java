package org.main.chatbot.domainmodels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@With
@Data
@AllArgsConstructor
public class Conversation {

    private String id;
    private String title;
    private List<Message> messages;
    private String summary;

    public Conversation() {
        this.id = UUID.randomUUID().toString();
        this.title=  "";
        this.messages = new ArrayList<>();
        this.summary = "";
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public String generateContext(String contextPrefix) {
        return contextPrefix.concat(System.lineSeparator()).concat(this.summary);
    }

    public String dumpConversation() {
        var sb = new StringBuilder();
        this.messages.forEach(message -> sb.append(message.getActor()).append(": ").append(message.getText()).append(System.lineSeparator()));
        return sb.toString();
    }
}
