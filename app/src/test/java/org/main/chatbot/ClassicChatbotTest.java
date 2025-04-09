package org.main.chatbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClassicChatbotTest {

    private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
    private BedrockRuntimeAsyncClient asyncBedrockClient;
    private ClassicChatbot chatbot;

    @BeforeEach
    void setup() {
        asyncBedrockClient = Mockito.mock(BedrockRuntimeAsyncClient.class);
        // Using the constructor that reads from System.in; this is okay for the input() test.
        chatbot = new ClassicChatbot(MODEL_ID, asyncBedrockClient);
    }

    /**
     * This test verifies that when calling the input method:
     * <ul>
     *   <li>the Bedrock client’s invokeModelWithResponseStream is called with a request whose body contains the prompt, and</li>
     *   <li>if no streaming chunks are simulated the method returns an empty string.</li>
     * </ul>
     */
    @Test
    void input_shouldInvokeBedrockClient_andReturnEmptyResponse_whenNoStreamEmitted() {
        // Arrange: stub the async client so that it returns a completed future immediately.
        when(asyncBedrockClient.invokeModelWithResponseStream(any(InvokeModelWithResponseStreamRequest.class),
                any(InvokeModelWithResponseStreamResponseHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        String prompt = "Write me a 100 words poem on computers";
        String result = chatbot.input(prompt);

        // Assert: Since no chunk is simulated, the accumulated response is empty.
        assertEquals("", result);

        // Also verify that the request body contains the prompt.
        ArgumentCaptor<InvokeModelWithResponseStreamRequest> requestCaptor =
                ArgumentCaptor.forClass(InvokeModelWithResponseStreamRequest.class);
        verify(asyncBedrockClient).invokeModelWithResponseStream(requestCaptor.capture(), any());
        String requestBody = requestCaptor.getValue().body().asUtf8String();
        assertTrue(requestBody.contains(prompt),
                "The request body should contain the prompt text.");
    }

    /**
     * This test verifies that the run() loop reads user input until "exit" is entered.
     * We simulate three lines of input:
     * <ul>
     *   <li>First prompt</li>
     *   <li>Second prompt</li>
     *   <li>"exit" to stop the loop</li>
     * </ul>
     * We then verify that the async client was called for each prompt (except the "exit" command).
     */
    @Test
    void run_shouldProcessMultiplePrompts_untilExit() {
        // Arrange: simulate user input with two prompts followed by "exit"
        String simulatedInput = "Prompt 1\nPrompt 2\nexit\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        // Create a chatbot instance that reads from our simulated input stream.
        ClassicChatbot chatbotWithInput = new ClassicChatbot(MODEL_ID, asyncBedrockClient, inputStream);

        // Stub the async client to return a completed future immediately.
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        when(asyncBedrockClient.invokeModelWithResponseStream(
                any(InvokeModelWithResponseStreamRequest.class),
                any(InvokeModelWithResponseStreamResponseHandler.class)
        )).thenReturn(future);

        // Act
        chatbotWithInput.run();

        // Assert: The client should be called for each non-exit prompt (here, twice).
        verify(asyncBedrockClient, times(3)).invokeModelWithResponseStream(
                any(InvokeModelWithResponseStreamRequest.class),
                any(InvokeModelWithResponseStreamResponseHandler.class)
        );
    }

}
