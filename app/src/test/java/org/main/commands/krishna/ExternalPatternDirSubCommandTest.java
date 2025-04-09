package org.main.commands.krishna;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.chatbot.ClassicChatbot;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ExternalPatternDirSubCommandTest {

    private final ClassicChatbot classicChatbot = Mockito.mock(ClassicChatbot.class);
    private ExternalPatternDirSubCommand externalPatternDirSubCommand;

    @BeforeEach
    void setUp() {
        this.externalPatternDirSubCommand = new ExternalPatternDirSubCommand(classicChatbot);
    }

    @Test
    void testExternalPathDirectoryForPatterns() {
        // Simulate an external pattern file with some known content.
        String externalFileContent = "External pattern content\nLine2";
        // Simulate some input from System.in.
        String stdinContent = "stdin input";
        System.setIn(new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8)));

        // Use static mocking for Files
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            // --- system context file ---
            Path externalDirPath = Paths.get("dummyDir");
            filesMock.when(() -> Files.exists(externalDirPath)).thenReturn(true);
            Path externalPatternPath = externalDirPath.resolve(String.format("%s/system.md", "testpattern"));
            filesMock.when(() -> Files.exists(externalPatternPath)).thenReturn(true);
            // When newBufferedReader is called, return a reader over our fake external file content.
            filesMock.when(() -> Files.newBufferedReader(externalPatternPath))
                    .thenReturn(new BufferedReader(new StringReader(externalFileContent)));

            // Act
            int result = new CommandLine(externalPatternDirSubCommand).execute("dummyDir", "-p", "testpattern");
            assertEquals(0, result);

            // Assert that the chatbot.input() was called with a string that contains:
            // – the external file’s content,
            // – the System.in content, and
            // – the user prompt.
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(classicChatbot, times(1)).input(captor.capture());
            String actualInput = captor.getValue();
            assertTrue(actualInput.contains(externalFileContent));
            assertTrue(actualInput.contains("Line2"));
            assertTrue(actualInput.contains("stdin input"));
        }
    }
}
