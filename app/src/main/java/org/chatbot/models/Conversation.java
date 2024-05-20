package org.chatbot.models;

import java.util.List;
import java.util.stream.Collectors;

public record Conversation(List<MessageExchange> messageExchangeList) {
    public void append(MessageExchange messageExchange) {
        this.messageExchangeList.add(messageExchange);
    }

    public String toPrompt(String systemPrompt) {
        if (messageExchangeList.isEmpty()) return "";

        return systemPrompt.concat(System.lineSeparator())
                .concat(messageExchangeList.stream()
                        .map(exchange -> "User:".concat(exchange.getUserMessage()).concat(System.lineSeparator()).concat("You:").concat(exchange.getChatbotMessage()).concat(System.lineSeparator()))
                        .collect(Collectors.joining(System.lineSeparator())));
    }

    public String pretty() {
        return "Conversation History".concat(System.lineSeparator()).concat(messageExchangeList.stream().map(exchange -> """
                --
                User: %s,
                You: %s
                --
                """.formatted(exchange.getUserMessage(), exchange.getChatbotMessage())).collect(Collectors.joining(System.lineSeparator())));
    }
}
