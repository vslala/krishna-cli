package org.main.commands;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.chatbot.fixtures.TestUtil;
import org.models.TodoTask;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final Path TODO_PATH = Path.of("/tmp/.krishna/todo/%s".formatted(LocalDate.now().toString()));

    @BeforeEach
    public void setUpStreams() throws IOException {
        TestUtil.deleteDirectory(TODO_PATH.getParent());
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void should_create_a_new_todo_list() {
        new CommandLine(new Todo()).execute("-create", "New Task");
        assertTrue(Files.exists(TODO_PATH));
    }

    @Test
    void should_print_the_todo_list() throws IOException {
        if (Files.exists(TODO_PATH.resolve("task-1.json")))
            Files.deleteIfExists(TODO_PATH.resolve("task-1.json"));

        Files.createDirectories(TODO_PATH);
        Files.writeString(TODO_PATH.resolve("task-1.json"),
                new JSONObject(new TodoTask("task-1", "New Task", false)).toString());

        new CommandLine(new Todo()).execute("ls");
        String output = outContent.toString();

        // Assert the output contains the expected task with strikethrough
        String expectedOutput = "[task-1] New Task\n";
        assertTrue(output.contains(expectedOutput));
    }

    @Test
    void should_mark_the_task_as_complete() {
        should_create_a_new_todo_list();
        new CommandLine(new Todo()).execute("-done", "task-1");
        var task = new TodoTask("task-1", "New Task", true);
        assertTrue(outContent.toString().contains(task.formattedTask()));
    }

    @Test
    void it_should_move_the_open_task_from_previous_day_to_today() throws IOException {
        Path yesterdayTodoDir = Path.of("/tmp/.krishna/todo/%s".formatted(LocalDate.now().minusDays(1).toString()));
        TestUtil.deleteDirectory(yesterdayTodoDir);
        Files.createDirectories(yesterdayTodoDir);
        Files.writeString(yesterdayTodoDir.resolve("task-1.json"), new JSONObject(new TodoTask("task-1", "Yesterday Task", false)).toString());

        TestUtil.deleteDirectory(Path.of("/tmp/.krishna/todo/%s".formatted(LocalDate.now().toString())));

        new CommandLine(new Todo()).execute("-create", "New task for the day");

        new CommandLine(new Todo()).execute("ls");

        var consoleOutput = outContent.toString();

        assertTrue(consoleOutput.contains("[task-1]"));
        assertTrue(consoleOutput.contains("[task-2]"));
    }
}
