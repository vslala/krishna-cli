package org.chatbot.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    @Test
    void should_return_the_conversation_prompt() {
        var conversation = new Conversation(List.of(
                new MessageExchange("this is query by user", "this is the reply from chatbot"),
                new MessageExchange("this is query by user", "this is the reply from chatbot"),
                new MessageExchange("this is query by user", "this is the reply from chatbot")
        ));

        String context = conversation.toPrompt("Make sure to answer in the context of this conversation below:\n");

        assertEquals("""
                Make sure to answer in the context of this conversation below:

                User Prompt:this is query by user
                Chatbot Response:this is the reply from chatbot
                
                User Prompt:this is query by user
                Chatbot Response:this is the reply from chatbot
                
                User Prompt:this is query by user
                Chatbot Response:this is the reply from chatbot
                """, context);
    }
}
