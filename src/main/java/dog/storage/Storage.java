package dog.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import dog.task.Deadline;
import dog.task.Event;
import dog.task.Task;
import dog.task.Todo;

/**
 * Loads and saves tasks using a text file on the local file system.
 */
public class Storage {
    private final Path filePath;
    private int skippedLineCount;

    /**
     * Creates storage that reads from and writes to the given path.
     *
     * @param filePath Relative path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
        this.skippedLineCount = 0;
    }

    /**
     * Loads valid tasks into the supplied array and skips corrupted lines.
     *
     * @param tasks Destination array for loaded tasks.
     * @return Number of tasks loaded.
     * @throws IOException If the existing file cannot be read.
     */
    public int loadTasks(Task[] tasks) throws IOException {
        skippedLineCount = 0;
        if (!Files.exists(filePath)) {
            return 0;
        }

        int taskCount = 0;
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            if (taskCount == tasks.length) {
                skippedLineCount++;
                continue;
            }
            try {
                tasks[taskCount] = parseTask(line);
                taskCount++;
            } catch (IllegalArgumentException e) {
                skippedLineCount++;
            }
        }
        return taskCount;
    }

    /**
     * Saves the current tasks, creating the parent directory when needed.
     *
     * @param tasks Tasks to save.
     * @param taskCount Number of populated entries in the task array.
     * @throws IOException If the data cannot be written.
     */
    public void saveTasks(Task[] tasks, int taskCount) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> lines = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            lines.add(formatTask(tasks[i]));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    private Task parseTask(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3) {
            throw new IllegalArgumentException("Missing task fields");
        }

        Task task;
        String description = decode(fields[2]);
        switch (fields[0]) {
        case "T":
            requireFieldCount(fields, 3);
            task = new Todo(description);
            break;
        case "D":
            requireFieldCount(fields, 4);
            task = new Deadline(description, decode(fields[3]));
            break;
        case "E":
            requireFieldCount(fields, 5);
            task = new Event(description, decode(fields[3]), decode(fields[4]));
            break;
        default:
            throw new IllegalArgumentException("Unknown task type");
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new IllegalArgumentException("Invalid task status");
        }
        return task;
    }

    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String commonFields = status + "|" + encode(task.getDescription());
        if (task instanceof Deadline deadline) {
            return "D|" + commonFields + "|" + encode(deadline.getBy());
        }
        if (task instanceof Event event) {
            return "E|" + commonFields + "|" + encode(event.getFrom()) + "|" + encode(event.getTo());
        }
        return "T|" + commonFields;
    }

    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Unexpected task fields");
        }
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
