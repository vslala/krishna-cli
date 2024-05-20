package org.chatbot.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageExchange {
    private String userMessage;
    private String chatbotMessage;

}
