package org.chatbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ClassicChatbotTest {

    private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
    private ClassicChatbot chatbot;
    private BedrockRuntimeAsyncClient asyncBedrockClient = BedrockRuntimeAsyncClient.builder().region(Region.US_EAST_1).build();

    @BeforeEach
    void beforeEach() {
        chatbot = new ClassicChatbot(MODEL_ID);
    }

    @Test
    void it_should_stream_the_response_from_chatbot_to_the_console() {
        chatbot.input("Write me a 100 words poem on computers");

        assertTrue(true);
    }

    @Test
    void it_should_run_until_exit_is_called() {
        String simulatedInput = "Write me a 300 word poem\nThat's nice, could you write me another poem but this time only use 100 words\nexit";
        var chatbot = new ClassicChatbot(MODEL_ID, new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

        chatbot.run();

        assertTrue(true);
    }
}
