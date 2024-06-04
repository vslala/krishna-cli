package org.chatbot;

import org.chatbot.models.ChatSession;
import org.chatbot.models.Conversation;
import org.chatbot.models.MessageExchange;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class ChatSessionManager {

    private String chatSessionDir;

    public ChatSessionManager(String chatSessionDir) {
        this.chatSessionDir = chatSessionDir;
    }

    public ChatSession createNewChatSession() {
        String uniqId = UUID.randomUUID().toString();
        String fileName = uniqId.concat(".dat");
        try {
            Path sessionPath = Path.of(chatSessionDir.concat("/").concat(fileName));
            Files.createFile(sessionPath);
            return new ChatSession(uniqId, "Title - ".concat(uniqId), new Conversation(new ArrayList<>()), sessionPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ChatSession> listLastNSessions(int lastNMessage) {
        try {
            List<Path> lastNSessionPaths = Files.list(Path.of(chatSessionDir)).filter(Files::isRegularFile)
                    .sorted((path1, path2) -> {
                        try {
                            long t1 = Files.readAttributes(path1, BasicFileAttributes.class).lastModifiedTime().toMillis();
                            long t2 = Files.readAttributes(path2, BasicFileAttributes.class).lastModifiedTime().toMillis();
                            return Long.compare(t2, t1);
                        } catch (IOException e) {
                            throw new RuntimeException("Cannot sort the files!");
                        }
                    })
                    .limit(lastNMessage)
                    .toList();

            return lastNSessionPaths.stream().map(sessionPath -> {
                        String fileName = sessionPath.getFileName().toString();
                        String sessionId = fileName.split("\\.")[0];
                        String sessionTitle = "Title - ".concat(sessionId);
                        try {
                            List<MessageExchange> messages = Files.readAllLines(sessionPath)
                                    .stream().map(exchangeText -> {
                                        var exchangeObj = new JSONObject(exchangeText);
                                        String userMessage = exchangeObj.has("userMessage") ? exchangeObj.get("userMessage").toString() : "{}";
                                        String chatbotMessage = exchangeObj.has("chatbotMessage") ? exchangeObj.get("chatbotMessage").toString() : "{}";
                                        return new MessageExchange(userMessage, chatbotMessage);
                                    })
                                    .collect(Collectors.toList());
                            return new ChatSession(sessionId, sessionTitle, new Conversation(messages), sessionPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
