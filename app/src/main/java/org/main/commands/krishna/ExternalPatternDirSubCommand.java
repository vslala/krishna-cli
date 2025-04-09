package org.main.commands.krishna;

import lombok.SneakyThrows;
import org.main.chatbot.ClassicChatbot;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@CommandLine.Command(
        name = "epd",
        mixinStandardHelpOptions = true,
        description = "Takes in external pattern directory"
)
class ExternalPatternDirSubCommand implements BaseCommand {
    private final ClassicChatbot classicChatbot;
    private String promptTemplate = "";

    public ExternalPatternDirSubCommand(ClassicChatbot classicChatbot) {
        this.classicChatbot = classicChatbot;
    }

    @CommandLine.Parameters(index = "0", description = "Takes in external pattern directory path")
    String externalPatternDir;

    @CommandLine.Option(names = "-prompt", description = "Additional prompt provided by user", defaultValue = "")
    String userPrompt;

    @SneakyThrows
    @CommandLine.Option(names = {"-p", "--pattern"})
    public void readPattern(String pattern) {
        StringBuilder sb = new StringBuilder();
        Path externalDirPath = Path.of(externalPatternDir);
        if (Files.exists(externalDirPath)) {
            Path externalPatternPath = externalDirPath.resolve(String.format("%s/system.md", pattern));
            if (Files.exists(externalPatternPath)) {
                try (BufferedReader br = Files.newBufferedReader(externalPatternPath)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                }
            }
        }

        this.promptTemplate = sb.toString();
    }

    @Override
    public Integer call() throws Exception {
        var sb = new StringBuilder();

        String stdIn = readStdIn();
        if (!stdIn.isEmpty()) {
            sb.append(stdIn);
        }
        if (!this.promptTemplate.isEmpty()) {
            sb.append(promptTemplate);
        }
        if (!userPrompt.isEmpty()) {
            sb.append(userPrompt);
        }

        this.classicChatbot.input(sb.toString());
        return 0;
    }
}
