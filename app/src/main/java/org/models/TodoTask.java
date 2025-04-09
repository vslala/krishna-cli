package org.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoTask {
    // ANSI escape codes for strikethrough and reset
    private static final String UNCHECKED_BOX = "\u2610";
    private static final String GREEN = "\u001B[32m";
    private static final String CHECK_MARK = "\u2713"; // Unicode for check mark
    private static final String STRIKETHROUGH = "\u001B[9m";
    private static final String RESET = "\u001B[0m";

    private String id;
    private String task;
    private boolean done;

    public String formattedTask() {
        task = done ? "%s [%s] %s %s".formatted(GREEN + CHECK_MARK, id, task, RESET) : "%s [%s] %s".formatted(UNCHECKED_BOX, id, task);

        return task;
    }
}
