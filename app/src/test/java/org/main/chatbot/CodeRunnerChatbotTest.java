package org.main.chatbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
class CodeRunnerChatbotTest {

    private CodeRunnerChatbot codeRunnerChatbot;
    private BedrockRuntimeAsyncClient asyncBedrockClient = BedrockRuntimeAsyncClient.builder().region(Region.US_EAST_1).build();
    private ClassicChatbot classicChatbot = new ClassicChatbot("anthropic.claude-3-sonnet-20240229-v1:0", asyncBedrockClient);
    private ConversationManager conversationManager = Mockito.mock(ConversationManager.class);
    @BeforeEach
    void beforeEach() {
        codeRunnerChatbot = new CodeRunnerChatbot(classicChatbot, conversationManager);
    }

    @Test
    void should_check_to_see_if_code_execution_could_help_better_solve_the_users_query() {
        String response = codeRunnerChatbot.input("How is the weather today?");

        System.out.println(response);

        assertEquals("Yes", response);
    }

    @Test
    void provide_the_python_code_for_execution_to_answer_the_user_query() {
        String response = codeRunnerChatbot.input("""
                Please summarise the following article for me: https://www.bemyaficionado.com/top-5-common-design-patterns/
                """);

        System.out.println(response);
        assertNotNull(response);
    }
}
