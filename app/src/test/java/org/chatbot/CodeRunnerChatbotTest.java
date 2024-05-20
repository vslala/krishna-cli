package org.chatbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeRunnerChatbotTest {

    private CodeRunnerChatbot codeRunnerChatbot;

    @BeforeEach
    void beforeEach() {
        codeRunnerChatbot = new CodeRunnerChatbot("anthropic.claude-3-sonnet-20240229-v1:0");
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
