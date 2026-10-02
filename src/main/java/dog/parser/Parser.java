package dog.parser;

import dog.task.Deadline;
import dog.task.Event;
import dog.task.Task;
import dog.task.Todo;

/**
 * Interprets task descriptions and task numbers without performing input or output.
 */
public final class Parser {
    private static final String TODO_ERROR = "OOPS!!! A todo needs a description.";
    private static final String DEADLINE_ERROR = "OOPS!!! A deadline needs a description and a /by date or time.";
    private static final String EVENT_ERROR = "OOPS!!! An event needs a description, /from time, and /to time.";
    private static final String UNKNOWN_COMMAND_ERROR = "OOPS!!! I don't know what that command means.";

    private Parser() {
    }

    /**
     * Converts a user-facing task number into a valid zero-based index.
     */
    public static int parseTaskIndex(String taskNumber, int taskCount) {
        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please provide a valid task number.", e);
        }
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new IllegalArgumentException("That task number does not exist.");
        }
        return taskIndex;
    }

    /**
     * Creates a task from an add-command word and its trimmed arguments.
     *
     * @throws IllegalArgumentException If the command or required fields are invalid.
     */
    public static Task createTask(String commandWord, String arguments) {
        if (commandWord.equalsIgnoreCase("todo")) {
            if (arguments.isEmpty()) {
                throw new IllegalArgumentException(TODO_ERROR);
            }
            return new Todo(arguments);
        }

        if (commandWord.equalsIgnoreCase("deadline")) {
            return createDeadline(arguments);
        }

        if (commandWord.equalsIgnoreCase("event")) {
            return createEvent(arguments);
        }

        throw new IllegalArgumentException(UNKNOWN_COMMAND_ERROR);
    }

    private static Task createDeadline(String arguments) {
        String[] deadlineFields = splitRequiredFields(arguments, "\\s+/by\\s+", DEADLINE_ERROR);
        return new Deadline(deadlineFields[0], deadlineFields[1]);
    }

    private static Task createEvent(String arguments) {
        String[] eventFields = splitRequiredFields(arguments, "\\s+/from\\s+", EVENT_ERROR);
        String[] timeRange = splitRequiredFields(eventFields[1], "\\s+/to\\s+", EVENT_ERROR);
        return new Event(eventFields[0], timeRange[0], timeRange[1]);
    }

    /**
     * Splits at the first delimiter and requires nonempty text on both sides.
     * Date text remains uninterpreted, including subsequent delimiters.
     */
    private static String[] splitRequiredFields(String arguments, String delimiterPattern, String usage) {
        String[] fields = arguments.split(delimiterPattern, 2);
        if (fields.length != 2) {
            throw new IllegalArgumentException(usage);
        }
        String firstField = fields[0].trim();
        String secondField = fields[1].trim();
        if (firstField.isEmpty() || secondField.isEmpty()) {
            throw new IllegalArgumentException(usage);
        }
        return new String[] {firstField, secondField};
    }
}
