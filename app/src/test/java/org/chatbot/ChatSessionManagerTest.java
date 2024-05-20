package org.chatbot;

import org.chatbot.models.ChatSession;
import org.chatbot.models.MessageExchange;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatSessionManagerTest {

    private static final String TEST_DIR = "/tmp/chat_sessions";

    @Test
    void should_create_new_chat_session() {
        var sessionManager = new ChatSessionManager(TEST_DIR);
        ChatSession chatSession = sessionManager.createNewChatSession();

        assertNotNull(chatSession.getSessionId());
        assertNotNull(chatSession.getSessionTitle());
        assertEquals(0, chatSession.getConversation().messageExchangeList().size());
    }

    @Test
    void should_add_messages_to_the_conversation() throws IOException {
        var sessionManager = new ChatSessionManager(TEST_DIR);
        ChatSession chatSession = sessionManager.createNewChatSession();
        chatSession.addExchange(new MessageExchange("The user sends a prompt!", "The chatbot replies"));
        chatSession.addExchange(new MessageExchange("The user sends another prompt!", "The chatbot replies yet again!"));

        List<String> fileData = Files.readAllLines(chatSession.getStoragePath());
        assertNotNull(fileData);
        assertEquals(2, fileData.size());
    }

    @Test
    void should_list_last_n_conversations() {
        var sessionManager = new ChatSessionManager(TEST_DIR);
        List<ChatSession> chatSessions = sessionManager.listLastNSessions(10);

        assertFalse(chatSessions.isEmpty());
    }

}
