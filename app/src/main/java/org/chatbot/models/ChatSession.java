package org.chatbot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Data
@AllArgsConstructor
public class ChatSession {
    private String sessionId;
    private String sessionTitle;
    private Conversation conversation;
    private Path storagePath;

    public void addExchange(MessageExchange messageExchange) {
        try {
            conversation.append(messageExchange);
            String messageExchangeText = new JSONObject(messageExchange).toString();
            Files.writeString(storagePath, messageExchangeText
                    .concat(System.lineSeparator()), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
