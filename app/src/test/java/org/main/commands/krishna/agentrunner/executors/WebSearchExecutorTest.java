package org.main.commands.krishna.agentrunner.executors;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.main.commands.krishna.agentrunner.AgentContext;
import org.main.commands.krishna.agentrunner.models.ShortTermMemory;
import org.main.commands.krishna.agentrunner.repositories.WorkflowRepository;
import org.main.commands.krishna.agentrunner.repositories.entities.WorkflowEntity;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

class WebSearchExecutorTest {

    private static final String llmThoughts = """
            [
              { "thought": "I need to search for the stock price", "action": "WEB_SEARCH" },
              { "thought": "Now I should parse the result", "action": "PARSE_RESULT" },
              { "thought": "Finally, I will respond to the user", "action": "RESPOND" }
            ]
            """;

    private WorkflowRepository workflowRepository = Mockito.mock(WorkflowRepository.class);

    @Test
    void should_perform_web_search() {
        var agentContext = new AgentContext(workflowRepository);
        Mockito.when(workflowRepository.findById("test-01")).thenReturn(buildWorkflowEntity());

        WebSearchExecutor executor = new WebSearchExecutor("https://api.search.brave.com", "BSAKUbGjj8kACckHyttFO4NeK70kLul");
        executor.execute("test-01", agentContext);

        var captor = ArgumentCaptor.forClass(WorkflowEntity.class);
        Mockito.verify(workflowRepository).save(captor.capture());
        System.out.println(captor.getValue());
    }

    private static Optional<WorkflowEntity> buildWorkflowEntity() {
        var e = new WorkflowEntity();
        e.setId("test-01");
        e.setUserPrompt("What is the share price of Amazon?");
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        e.setShortTermMemory(new JSONObject(
                ShortTermMemory.builder()
                        .currentOutput(llmThoughts)
                        .currentState("GENERATE_CHAIN_OF_THOUGHTS")
                        .build()).toString());
        return Optional.of(e);
    }
}
