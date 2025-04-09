package org.main.commands.krishna;

import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Component
@CommandLine.Command(
        name = "list",
        mixinStandardHelpOptions = true,
        description = "List all available patterns"
)
public class ListPatternSubCommand implements Callable<Integer> {

    final String UNCHECKED_BOX = "☐";

    @CommandLine.Option(names = {"-epd", "--external-pattern-dir"}, description = "External directory for patterns")
    String externalPatternDir;

    @Override
    public Integer call() throws Exception {
        // Check if externalPatternDir is provided and if pattern file exists there
        if (externalPatternDir != null) {
            Path externalDirPath = Path.of(externalPatternDir);
            if (Files.exists(externalDirPath)) {
                Files.list(externalDirPath)
                        .forEach(path -> System.out.println(UNCHECKED_BOX.concat(" ").concat(path.getFileName().toString())));
            }
        } else {
            URL resource = getClass().getClassLoader().getResource("patterns");
            if (resource != null) {
                Path patternsPath = Paths.get(resource.toURI());
                try (Stream<Path> paths = Files.list(patternsPath)) {
                    paths.filter(Files::isDirectory)
                            .forEach(path -> System.out.println(UNCHECKED_BOX.concat(" ").concat(path.getFileName().toString())));
                }
            } else {
                throw new IOException("Patterns directory not found in the internal resources.");
            }
        }
        return 0;
    }
}
