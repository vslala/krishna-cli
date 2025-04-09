package org.main.commands.krishna;

import lombok.SneakyThrows;
import org.main.chatbot.ClassicChatbot;
import org.main.chatbot.ConversationChatbot;
import org.main.commands.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
@CommandLine.Command(
        name = "krishna",
        mixinStandardHelpOptions = true,
        subcommands = {Todo.class, ListPatternSubCommand.class, PythonCodeAgentSubCommand.class, SystemContextSubCommand.class, ExternalPatternDirSubCommand.class},
        version = "krishna 0.1"
)
public class Krishna implements BaseCommand {
    private final ClassicChatbot classicChatbot;
    private final ConversationChatbot conversationChatbot;
    private String promptTemplate = "";
    private String pattern;
    private boolean startConversation;
    private String userPrompt;

    @CommandLine.Parameters(index = "0", description = "The main input string", defaultValue = "")
    private String userPromptWithoutOption;

    // TODO: will generate the image as per description
    @CommandLine.Option(names = {"-img", "--image"}, description = "Generates image with the given prompt")
    private boolean generateImage;

    @Autowired
    Krishna(ClassicChatbot classicChatbot, ConversationChatbot conversationChatbot) {
        this.classicChatbot = classicChatbot;
        this.conversationChatbot = conversationChatbot;
    }

    @SneakyThrows
    @CommandLine.Option(names = {"-p", "--pattern"}, description = "Prompt to be used with the input")
    public void loadPromptTemplate(String pattern) {
        this.pattern = pattern;
        var sb = new StringBuilder();
        InputStream inputPatternStream = getClass().getResourceAsStream(String.format("/patterns/%s/system.md", pattern));
        if (inputPatternStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputPatternStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            }
        } else {
            throw new KrishnaException("Provided pattern %s does not exists".formatted(pattern));
        }
        this.promptTemplate = sb.toString();
    }


    @CommandLine.Option(names = {"-sc", "--start-conversation"}, description = "Starts a conversation with the chatbot")
    public void setStartConversation(boolean startConversation) {
        this.startConversation = startConversation;
    }

    @CommandLine.Option(names = {"-prompt", "--prompt"}, description = "Adds user prompt after the pattern", defaultValue = "")
    public void setUserPromptWithoutOption(String userPromptWithoutOption) {
        this.userPrompt = userPromptWithoutOption;
    }

    @Override
    public Integer call() throws Exception {
        StringBuilder sb = new StringBuilder();
        if (this.startConversation) {
            this.conversationChatbot.startConversation();
            return 0;
        }
        // if std-in contains text
        String stdIn = readStdIn();

        if (!promptTemplate.isEmpty())
            sb.append(promptTemplate);

        sb.append(stdIn);

        if (!userPromptWithoutOption.isEmpty())
            sb.append(userPromptWithoutOption);
        else
            sb.append(userPrompt);

        if (!sb.isEmpty())
            this.classicChatbot.input(sb.toString());
        else
            throw new KrishnaException("No input provided!");

        return 0;
    }


}

