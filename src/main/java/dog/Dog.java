package dog;

import java.util.Scanner;

/**
 * Runs the Dog chatbot and handles commands entered by the user.
 */
public class Dog {
    private static final int MAX_TASKS = 100;
    private static final String DIVIDER = "____________________________________________________________";
    private static final String DEADLINE_USAGE = "Usage: deadline <description> /by <date/time>";
    private static final String EVENT_USAGE = "Usage: event <description> /from <start> /to <end>";

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments supplied to the program.
     */
    public static void main(String[] args) {
        printGreeting();

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String command = input.trim();

            if (command.equalsIgnoreCase("bye")) {
                System.out.println("Woof! See you again!");
                break;
            }

            try {
                taskCount = executeCommand(command, tasks, taskCount);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }

    /**
     * Executes a non-exit command and returns the resulting task count.
     * Invalid commands leave the task list unchanged.
     */
    private static int executeCommand(String command, Task[] tasks, int taskCount) {
        if (command.equalsIgnoreCase("list")) {
            printTaskList(tasks, taskCount);
            return taskCount;
        }

        String[] commandParts = command.split("\\s+", 2);
        String commandWord = commandParts[0];
        String arguments = commandParts.length == 2 ? commandParts[1] : "";
        if (commandWord.equalsIgnoreCase("mark") || commandWord.equalsIgnoreCase("unmark")) {
            updateTaskStatus(commandWord, arguments, tasks, taskCount);
            return taskCount;
        }

        if (taskCount == MAX_TASKS) {
            throw new IllegalArgumentException("I cannot store more than " + MAX_TASKS + " tasks.");
        }
        Task task = createTask(commandWord, arguments.trim());
        tasks[taskCount] = task;
        int updatedTaskCount = taskCount + 1;
        printAddedTask(task, updatedTaskCount);
        return updatedTaskCount;
    }

    /**
     * Updates a selected task only after validating its one-based number.
     */
    private static void updateTaskStatus(String commandWord, String arguments, Task[] tasks, int taskCount) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Please provide a task number, for example: "
                    + commandWord.toLowerCase() + " 1");
        }
        int taskIndex = parseTaskIndex(arguments, taskCount);
        boolean shouldMarkAsDone = commandWord.equalsIgnoreCase("mark");
        if (shouldMarkAsDone) {
            tasks[taskIndex].markAsDone();
        } else {
            tasks[taskIndex].markAsNotDone();
        }
        printTaskStatus(tasks[taskIndex], taskIndex + 1, shouldMarkAsDone);
    }

    /**
     * Converts a user-facing task number into a valid array index.
     */
    private static int parseTaskIndex(String taskNumber, int taskCount) {
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

    private static void printGreeting() {
        String banner = " ____              \n"
                + "|  _ \\  ___   __ _ \n"
                + "| | | |/ _ \\ / _` |\n"
                + "| |_| | (_) | (_| |\n"
                + "|____/ \\___/ \\__, |\n"
                + "             |___/ \n";
        System.out.println(banner);
        System.out.println("Woof! What can I do for you today?");
    }

    private static void printTaskList(Task[] tasks, int taskCount) {
        if (taskCount == 0) {
            System.out.println("Your task list is empty :(");
            return;
        }
        System.out.println(DIVIDER);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
        System.out.println(DIVIDER);
    }

    private static void printTaskStatus(Task task, int taskNumber, boolean isDone) {
        String action = isDone ? "marked as done" : "marked as not done";
        System.out.println("Task " + taskNumber + " has been " + action + ":");
        System.out.println(task);
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    private static Task createTask(String commandWord, String arguments) {
        if (commandWord.equalsIgnoreCase("todo")) {
            if (arguments.isEmpty()) {
                throw new IllegalArgumentException("Usage: todo <description>");
            }
            return new Todo(arguments);
        }

        if (commandWord.equalsIgnoreCase("deadline")) {
            return createDeadline(arguments);
        }

        if (commandWord.equalsIgnoreCase("event")) {
            return createEvent(arguments);
        }

        throw new IllegalArgumentException("Unknown command. Use todo, deadline, event, list, mark, unmark, or bye.");
    }

    private static Task createDeadline(String arguments) {
        String[] deadlineFields = splitRequiredFields(arguments, "\\s+/by\\s+", DEADLINE_USAGE);
        return new Deadline(deadlineFields[0], deadlineFields[1]);
    }

    private static Task createEvent(String arguments) {
        String[] eventFields = splitRequiredFields(arguments, "\\s+/from\\s+", EVENT_USAGE);
        String[] timeRange = splitRequiredFields(eventFields[1], "\\s+/to\\s+", EVENT_USAGE);
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
