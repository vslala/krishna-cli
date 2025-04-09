package org.main.commands.krishna;

import org.main.chatbot.CodeRunnerChatbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(
        name = "agent",
        mixinStandardHelpOptions = true,
        description = "Use agents to execute the code to provide better and accurate response"
)
public class PythonCodeAgentSubCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-lang", "--language"}, description = "takes user prompt for the given task")
    String language;

    @CommandLine.Option(names = {"-prompt", "--prompt"}, description = "takes user prompt for the given task")
    String userPrompt;

    private CodeRunnerChatbot codeRunnerChatbot;

    @Autowired
    private PythonCodeAgentSubCommand(CodeRunnerChatbot codeRunnerChatbot) {
        this.codeRunnerChatbot = codeRunnerChatbot;
    }

    @Override
    public Integer call() throws Exception {
        this.codeRunnerChatbot.input(userPrompt);
        return 0;
    }
}
