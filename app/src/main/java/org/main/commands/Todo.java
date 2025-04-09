package org.main.commands;

import org.json.JSONObject;
import org.models.TodoTask;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@CommandLine.Command(
        name = "todo",
        mixinStandardHelpOptions = true,
        subcommands = {Todo.ListTask.class},
        description = "Sub-command for creating todo list"
)
public class Todo implements Callable<Integer> {

    public static final String WORK_DIR = "/tmp/.krishna/todo/";
    LocalDate TODAY = LocalDate.now();
    Path CURRENT_TASK_DIR = Path.of(WORK_DIR);

    @CommandLine.Option(names = {"-create"}, defaultValue = "")
    String taskTitle;

    @CommandLine.Option(names = {"-done"}, defaultValue = "")
    String taskId;

    @Override
    public Integer call() throws Exception {

        if (!taskTitle.isEmpty()) {
            createDirectoryIfNotExists(CURRENT_TASK_DIR);
            boolean created = createDirectoryIfNotExists(CURRENT_TASK_DIR.resolve(TODAY.toString()));
            if (created && Files.exists(CURRENT_TASK_DIR.resolve(TODAY.minusDays(1).toString()))) {
                moveOpenTaskToToday();
            }

            List<Path> taskList = Files.list(CURRENT_TASK_DIR.resolve(TODAY.toString())).sorted().toList();
            int taskId = taskList.isEmpty() ? 1 : getNextSequenceNumber(taskList.getLast());
            String newTaskName = "task-%d.json".formatted(taskId);
            var task = new TodoTask(newTaskName.split("\\.")[0], taskTitle, false);
            var json = new JSONObject(task);

            Files.writeString(CURRENT_TASK_DIR.resolve(TODAY.toString()).resolve(newTaskName), json.toString(2));
        } else if (!taskId.isEmpty()) {
            Path taskPath = CURRENT_TASK_DIR.resolve(TODAY.toString()).resolve(taskId + ".json");
            var json = new JSONObject(Files.readString(taskPath));
            json.put("done", true);

            Files.writeString(taskPath, json.toString());
            listTasks(CURRENT_TASK_DIR, TODAY);
        }


        return 0;
    }

    private void moveOpenTaskToToday() throws IOException {
        List<TodoTask> prevDayOpenTasks = listTodoTasks(CURRENT_TASK_DIR, TODAY.minusDays(1))
                .stream().filter(task -> !task.isDone())
                .toList();

        prevDayOpenTasks.forEach(todoTask -> {
            try {
                Files.writeString(CURRENT_TASK_DIR.resolve(TODAY.toString()).resolve(todoTask.getId() + ".json"), new JSONObject(todoTask).toString(2));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean createDirectoryIfNotExists(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            return true;
        }

        return false;
    }

    private static List<TodoTask> listTodoTasks(Path dir, LocalDate now) throws IOException {
        if (Files.exists(dir.resolve(now.toString()))) {
            List<TodoTask> taskList = Files.list(dir.resolve(now.toString()))
                    .map(path -> {
                        try {
                            return new JSONObject(Files.readString(path));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(json -> new TodoTask(json.getString("id"), json.getString("task"), json.getBoolean("done")))
                    .sorted((t1, t2) -> Boolean.compare(t1.isDone(), t2.isDone()))
                    .toList();

            return taskList;
        } else {
            return List.of();
        }
    }

    private static void listTasks(Path dir, LocalDate now) throws IOException {
        List<TodoTask> taskList = listTodoTasks(dir, now);

        taskList.forEach(todoTask -> System.out.printf("%s%n", todoTask.formattedTask()));
    }

    private static int getNextSequenceNumber(Path path) {
        String fileName = path.getFileName().toString();

        Pattern pattern = Pattern.compile("task-(\\d+)\\.json");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1)) + 1;
        } else {
            throw new IllegalArgumentException("File name does not match the expected format: " + fileName);
        }
    }

    @CommandLine.Command(
            name = "ls",
            mixinStandardHelpOptions = true,
            description = "Sub-command for listing tasks"
    )
    public static class ListTask implements Callable<Integer> {

        public static final String WORK_DIR = "/tmp/.krishna/todo/";
        LocalDate TODAY = LocalDate.now();
        Path CURRENT_TASK_DIR = Path.of(WORK_DIR);

        @CommandLine.Option(names = {"-a"})
        boolean listAll;

        @Override
        public Integer call() throws Exception {
            if (listAll) {
                listTasks(CURRENT_TASK_DIR, TODAY);
            } else {
                var taskList = listTodoTasks(CURRENT_TASK_DIR, TODAY)
                        .stream().filter(todoTask -> !todoTask.isDone())
                        .toList();

                taskList.forEach(todoTask -> System.out.printf("%s%n", todoTask.formattedTask()));
            }

            return 0;
        }
    }
}
