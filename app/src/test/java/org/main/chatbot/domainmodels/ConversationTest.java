package org.main.chatbot.domainmodels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationTest {

    private Conversation conversation;

    @Mock
    private Message mockMessage;

    @BeforeEach
    void setUp() {
        conversation = new Conversation();
    }

    @Test
    void testConstructorWithNoArgs() {
        assertNotNull(conversation.getId());
        assertEquals("", conversation.getTitle());
        assertEquals("", conversation.getSummary());
        assertTrue(conversation.getMessages().isEmpty());
    }

    @Test
    void testAddMessage() {
        conversation.addMessage(mockMessage);
        assertEquals(1, conversation.getMessages().size());
        assertTrue(conversation.getMessages().contains(mockMessage));
    }

    @Test
    void testGenerateContext() {
        String contextPrefix = "Context Prefix";
        String expectedContext = contextPrefix + System.lineSeparator() + "";
        assertEquals(expectedContext, conversation.generateContext(contextPrefix));
    }

    @Test
    void testDumpConversation() {
        String actor = "User";
        String text = "Hello, World!";
        when(mockMessage.getActor()).thenReturn(actor);
        when(mockMessage.getText()).thenReturn(text);

        conversation.addMessage(mockMessage);

        String expected = actor + ": " + text + System.lineSeparator();
        assertEquals(expected, conversation.dumpConversation());
    }
}
