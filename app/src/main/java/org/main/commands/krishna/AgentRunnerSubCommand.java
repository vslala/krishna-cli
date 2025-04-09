package org.main.commands.krishna;

import org.json.JSONArray;
import org.main.chatbot.ClassicChatbot;
import org.main.commands.krishna.agentrunner.LoadPrompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(
        name = "agent",
        mixinStandardHelpOptions = true,
        description = "Executes agent-based workflow to achieve the said task"
)
public class AgentRunnerSubCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The main input string", defaultValue = "")
    private String userPromptWithoutOption;

    private final ClassicChatbot classicChatbot;

    @Autowired
    public AgentRunnerSubCommand(ClassicChatbot classicChatbot) {
        this.classicChatbot = classicChatbot;
    }

    @Override
    public Integer call() throws Exception {
        String chainOfThoughtsPrompt = LoadPrompt.byName("generate_chain_of_thoughts");
        var llmThoughts = new JSONArray(this.classicChatbot.input(chainOfThoughtsPrompt));



        return 0;
    }
}
