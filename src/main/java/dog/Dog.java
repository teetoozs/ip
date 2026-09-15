package dog;

import java.util.ArrayList;
import java.util.Scanner;

import dog.task.Deadline;
import dog.task.Event;
import dog.task.Task;
import dog.task.Todo;

/**
 * Runs the Dog chatbot and handles commands entered by the user.
 */
public class Dog {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String TODO_ERROR = "OOPS!!! A todo needs a description.";
    private static final String DEADLINE_ERROR = "OOPS!!! A deadline needs a description and a /by date or time.";
    private static final String EVENT_ERROR = "OOPS!!! An event needs a description, /from time, and /to time.";
    private static final String UNKNOWN_COMMAND_ERROR = "OOPS!!! I don't know what that command means.";

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments supplied to the program.
     */
    public static void main(String[] args) {
        printGreeting();

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String command = input.trim();

            if (command.equalsIgnoreCase("bye")) {
                System.out.println("Woof! See you again!");
                break;
            }

            try {
                executeCommand(command, tasks);
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            }
        }
        scanner.close();
    }

    /**
     * Executes a non-exit command.
     * Invalid commands leave the task list unchanged.
     */
    private static void executeCommand(String command, ArrayList<Task> tasks) {
        if (command.equalsIgnoreCase("list")) {
            printTaskList(tasks);
            return;
        }

        String[] commandParts = command.split("\\s+", 2);
        String commandWord = commandParts[0];
        String arguments = commandParts.length == 2 ? commandParts[1] : "";
        if (commandWord.equalsIgnoreCase("mark") || commandWord.equalsIgnoreCase("unmark")) {
            updateTaskStatus(commandWord, arguments, tasks);
            return;
        }
        if (commandWord.equalsIgnoreCase("delete")) {
            deleteTask(arguments, tasks);
            return;
        }

        Task task = createTask(commandWord, arguments.trim());
        tasks.add(task);
        printAddedTask(task, tasks.size());
    }

    /**
     * Deletes a selected task from the list.
     */
    private static void deleteTask(String arguments, ArrayList<Task> tasks) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Please provide a task number, for example: delete 1");
        }
        int taskIndex = parseTaskIndex(arguments, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        printDeletedTask(removedTask, tasks.size());
    }

    /**
     * Updates a selected task only after validating its one-based number.
     */
    private static void updateTaskStatus(String commandWord, String arguments, ArrayList<Task> tasks) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Please provide a task number, for example: "
                    + commandWord.toLowerCase() + " 1");
        }
        int taskIndex = parseTaskIndex(arguments, tasks.size());
        boolean shouldMarkAsDone = commandWord.equalsIgnoreCase("mark");
        if (shouldMarkAsDone) {
            tasks.get(taskIndex).markAsDone();
        } else {
            tasks.get(taskIndex).markAsNotDone();
        }
        printTaskStatus(tasks.get(taskIndex), taskIndex + 1, shouldMarkAsDone);
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

    private static void printTaskList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Your task list is empty :(");
            return;
        }
        System.out.println(DIVIDER);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
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

    private static void printDeletedTask(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    private static void printError(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    private static Task createTask(String commandWord, String arguments) {
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
