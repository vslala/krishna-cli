package org.main.commands.krishna;

import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Component
@CommandLine.Command(
        name = "context",
        mixinStandardHelpOptions = true,
        description = "Manages system context"
)
public class SystemContextSubCommand implements Callable<Integer> {

    public static final Path SYSTEM_CONTEXT_PATH = Path.of("/tmp/.krishna/system_context.md");

    @CommandLine.Option(names = {"-set", "--set"}, description = "Sets the context that will be passed with all forth coming prompts")
    String systemContext;

    @CommandLine.Option(names = {"-clr", "--clear"}, description = "Clears context")
    boolean clearContext;

    @Override
    public Integer call() throws Exception {
        if (clearContext) {
            Files.deleteIfExists(SYSTEM_CONTEXT_PATH);
        } else if (!systemContext.isEmpty()) {
            Files.writeString(SYSTEM_CONTEXT_PATH, systemContext);
        }

        return 0;
    }
}
