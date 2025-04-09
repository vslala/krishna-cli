package org.main.commands.krishna.agentrunner.executors;

import org.main.chatbot.ClassicChatbot;
import org.main.commands.krishna.agentrunner.AgentContext;
import org.main.commands.krishna.agentrunner.AgentExecutor;
import org.main.commands.krishna.agentrunner.LoadPrompt;
import org.main.commands.krishna.agentrunner.models.ShortTermMemory;
import org.main.commands.krishna.agentrunner.models.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChainOfThoughtsExecutor implements AgentExecutor {

    private final ClassicChatbot classicChatbot;

    @Autowired
    public ChainOfThoughtsExecutor(ClassicChatbot classicChatbot) {
        this.classicChatbot = classicChatbot;
    }

    @Override
    public void execute(String workflowId, AgentContext context) {
        Workflow workflow = context.get(workflowId);
        String llmThoughts = this.classicChatbot.input(LoadPrompt.byName("generate_chain_of_thoughts").replaceAll("\\{user_prompt}", workflow.getUserPrompt()));
        context.save(workflowId, ShortTermMemory.builder()
                        .currentOutput(llmThoughts)
                        .currentState("INIT")
                        .userPrompt(workflow.getUserPrompt())
                .build());
    }
}
