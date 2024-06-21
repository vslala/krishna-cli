package org.chatbot.commands;

import org.chatbot.ClassicChatbot;
import org.main.ConversationChatbot;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "krishna", mixinStandardHelpOptions = true, version = "krishna 0.1")
public class Krishna implements Callable<Integer> {
    private static final ClassicChatbot chatbot = new ClassicChatbot("anthropic.claude-3-sonnet-20240229-v1:0");

    @CommandLine.Option(names = {"-p", "--pattern"}, description = "Prompt to be used with the input", completionCandidates = PatternCompletion.class)
    String pattern;

    @CommandLine.Option(names = {"-sc", "--start-conversation"}, description = "Starts a conversation with the chatbot")
    boolean startConversation;

    @CommandLine.Option(names = {"-img", "--image"}, description = "Generates image with the given prompt")
    boolean generateImage;

    @CommandLine.Option(names = {"-prompt", "--prompt"}, description = "Adds user prompt after the pattern")
    String userPrompt;

    @CommandLine.Option(names = {"-ls", "--list-patterns"}, description = "List all available patterns")
    boolean listPatterns;

    @CommandLine.Option(names = {"-epd", "--external-pattern-dir"}, description = "External directory for patterns")
    String externalPatternDir;

    @Override
    public Integer call() throws Exception {
        if (startConversation) {
            var conversation = new ConversationChatbot();
        } else {
            StringBuilder sb = new StringBuilder();
            boolean patternLoaded = false;

            // Check if externalPatternDir is provided and if pattern file exists there
            if (externalPatternDir != null) {
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
                        patternLoaded = true;
                    }
                }
            }

            // If pattern not found in external directory, check in internal resources
            if (!patternLoaded) {
                var inputPatternStream = getClass().getResourceAsStream(String.format("/patterns/%s/system.md", pattern));
                if (inputPatternStream != null) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputPatternStream))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append(System.lineSeparator());
                        }
                    }
                    patternLoaded = true;
                } else {
                    throw new IOException("Pattern not found in the given location: " + (externalPatternDir != null ? externalPatternDir.toString() : "internal resources"));
                }
            }

            // stdin - if available
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (reader.ready() && (line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }

            sb.append(userPrompt);
            chatbot.input(sb.toString());
        }

        return 0;
    }

    public static class PatternCompletion implements Iterable<String> {
        private static final Path patternsPath = Path.of("src/main/resources/patterns");
        @Override
        public Iterator<String> iterator() {
            return Arrays.stream(Objects.requireNonNull(patternsPath.toFile().listFiles())).map(File::getName).iterator();
        }
    }
}
