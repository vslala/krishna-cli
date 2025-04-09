package org.main.chatbot;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.chatbot.domainmodels.Conversation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class ConversationChatbotTest {

    private Conversation mockConversation = mock(Conversation.class);
    private ClassicChatbot classicChatbot = mock(ClassicChatbot.class);
    private ConversationManager conversationManager = mock(ConversationManager.class);
    private ConversationChatbot conversationChatbot;

    private InputStream inputStream;
    private Scanner scanner;

    private Scanner resetInputStream(String input) {
        // Reset InputStream and Scanner with new input
        inputStream = new ByteArrayInputStream(input.getBytes());
        scanner = new Scanner(inputStream);
        return scanner;
    }

    @BeforeEach
    public void beforeEach() {
        var titleAndSummary = new JSONObject();
        titleAndSummary
                .put("title", "test title")
                .put("summary", "test summary");
        when(mockConversation.withTitle("test title")).thenReturn(mockConversation);
        when(mockConversation.withSummary("test summary")).thenReturn(mockConversation);
        when(conversationManager.startNewConversation()).thenReturn(mockConversation);
        when(classicChatbot.input(anyString())).thenReturn(titleAndSummary.toString());
        conversationChatbot = new ConversationChatbot(classicChatbot, conversationManager, scanner);
    }

    @Test
    void it_should_create_new_conversation() {
        conversationChatbot = new ConversationChatbot(classicChatbot, conversationManager, resetInputStream("1\nexit\nexit"));
        conversationChatbot.startConversation();

        verify(conversationManager, times(1)).startNewConversation();
        verify(this.mockConversation, times(1)).dumpConversation();
    }
}
