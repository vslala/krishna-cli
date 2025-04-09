package org.main.commands.krishna;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.chatbot.ClassicChatbot;
import org.main.chatbot.ConversationChatbot;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KrishnaTest {

    @Mock
    private ClassicChatbot mockClassicChatbot;

    @Mock
    private ConversationChatbot conversationChatbot;

    private static final Path SYSTEM_CONTEXT_PATH = Path.of("/tmp/.krishna/system_context.md");

    // The command under test. (Krishna is constructed via dependency injection.)
    private Krishna krishna;

    // Save original System.in/out so we can restore after tests
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Inject our mock chatbot into Krishna
        krishna = new Krishna(mockClassicChatbot, conversationChatbot);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * When the --start-conversation option is true,
     * the call() method should not invoke chatbot.input().
     */
    @Test
    void testCall_startConversationTrue() throws Exception {
        Mockito.reset(mockClassicChatbot);
        // Arrange: Provide dummy input to simulate the conversation.
        // Here we simulate that the user types "exit" immediately so that
        // ConversationChatbot.mainMenu() can exit gracefully.
        String conversationInput = "exit\n";
        System.setIn(new ByteArrayInputStream(conversationInput.getBytes(StandardCharsets.UTF_8)));

        // Instead of manually setting startConversation, we pass the command line parameter.
        // Note: In picocli, "--start-conversation" (or "-sc") sets the startConversation field to true.
        CommandLine cmd = new CommandLine(krishna);

        // Act: Execute the command as it would be from the CLI.
        int result = cmd.execute("-sc");

        // Assert: The command should return 0 and never call the chatbot's input() method.
        assertEquals(0, result);
        verify(mockClassicChatbot, never()).input(anyString());
    }

    /**
     * When no external pattern directory is provided, the internal resource should be used.
     * (In this test we assume that a test resource exists in src/test/resources/patterns/internalpattern/system.md.)
     */
    @Test
    void testCall_internalPatternFound() throws Exception {
        // For system context, simulate that the file does not exist.
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(KrishnaTest.SYSTEM_CONTEXT_PATH))
                    .thenReturn(false);
            // Let System.in be empty.
            System.setIn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));

            // Act. (This test assumes that in your test resources you have the file:
            int result = new CommandLine(krishna).execute("-p", "extract_ideas");
            assertEquals(0, result);
            verify(mockClassicChatbot, times(1)).input(anyString());
        }
    }

    /**
     * If neither an external pattern file nor an internal resource is found,
     * the call() method should throw an IOException.
     */
    @Test
    void testCall_noPatternFoundThrowsIOException() {
        // Act + Assert: since no internal resource should be found, call() throws IOException.
        assertThrows(KrishnaException.class, () -> {
            // Arrange: externalPatternDir is null and choose a pattern that does not exist internally.
            krishna.loadPromptTemplate("nonexistent");
            krishna.setUserPromptWithoutOption("User prompt");
            krishna.call();
        });
    }

    // ----- Tests for subcommands -----

    /**
     * Test the ListPattern subcommand when an external pattern directory is provided.
     * This test creates a temporary directory with dummy subdirectories.
     */
    @Test
    void testListPattern_externalDir() throws Exception {
        // Arrange
        ListPatternSubCommand listPattern = new ListPatternSubCommand();
        // Create a temporary directory with two fake pattern directories.
        Path tempDir = Files.createTempDirectory("patternsTest");
        try {
            Files.createDirectory(tempDir.resolve("pattern1"));
            Files.createDirectory(tempDir.resolve("pattern2"));
            listPattern.externalPatternDir = tempDir.toString();

            // Capture System.out output.
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            // Act
            int result = listPattern.call();
            assertEquals(0, result);
            String output = outContent.toString();
            // Assert that the names of our dummy directories appear in the output.
            assertTrue(output.contains("pattern1"));
            assertTrue(output.contains("pattern2"));
        } finally {
            // Clean up the temporary directory.
            try (Stream<Path> walk = Files.walk(tempDir)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                // ignore cleanup errors
                            }
                        });
            }
        }
    }

    @Test
    void itShouldReadTheUserPromptAsFirstParameter() {
        new CommandLine(krishna).execute("Write me a code in python");

        verify(mockClassicChatbot, times(1)).input("Write me a code in python");
    }

    @Test
    void itShouldAllowStdInAlongWithThePromptTemplate() {
        // Given Std Input
        System.setIn(new ByteArrayInputStream("a = a + b".getBytes(StandardCharsets.UTF_8)));

        new CommandLine(krishna).execute("-p", "analyze_code");

        var captor = ArgumentCaptor.forClass(String.class);
        verify(mockClassicChatbot, times(1)).input(captor.capture());

        String actualInput = captor.getValue();
        assertTrue(actualInput.contains("# PROVIDED CONTEXT"));
        assertTrue(actualInput.contains("a = a + b"));
        assertTrue(actualInput.contains("# CODE ANALYSIS GUIDELINE"));

    }
}
