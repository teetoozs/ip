package dog;

import java.util.Scanner;

/**
 * Runs the Dog chatbot and handles commands entered by the user.
 */
public class Dog {
    private static final int MAX_TASKS = 100;
    private static final String DIVIDER = "____________________________________________________________";

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

            if (command.equalsIgnoreCase("list")) {
                printTaskList(tasks, taskCount);
                continue;
            }

            String[] commandParts = command.split("\\s+", 2);
            String commandWord = commandParts[0];
            if (commandWord.equalsIgnoreCase("mark") || commandWord.equalsIgnoreCase("unmark")) {
                if (commandParts.length < 2) {
                    System.out.println("Please provide a task number, for example: "
                            + commandWord.toLowerCase() + " 1");
                    continue;
                }

                int taskIndex;
                try {
                    taskIndex = Integer.parseInt(commandParts[1]) - 1;
                } catch (NumberFormatException e) {
                    System.out.println("Please provide a valid task number.");
                    continue;
                }

                if (taskIndex < 0 || taskIndex >= taskCount) {
                    System.out.println("That task number does not exist.");
                    continue;
                }

                boolean shouldMarkAsDone = commandWord.equalsIgnoreCase("mark");
                if (shouldMarkAsDone) {
                    tasks[taskIndex].markAsDone();
                } else {
                    tasks[taskIndex].markAsNotDone();
                }
                printTaskStatus(tasks[taskIndex], taskIndex + 1, shouldMarkAsDone);
                continue;
            }

            if (taskCount == MAX_TASKS) {
                System.out.println("I cannot store more than " + MAX_TASKS + " tasks.");
                continue;
            }

            String arguments = commandParts.length == 2 ? commandParts[1].trim() : "";
            Task task;
            try {
                task = createTask(commandWord, arguments);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            tasks[taskCount] = task;
            taskCount++;
            printAddedTask(task, taskCount);
        }
        scanner.close();
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
            String[] parts = arguments.split("\\s+/by\\s+", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                throw new IllegalArgumentException("Usage: deadline <description> /by <date/time>");
            }
            return new Deadline(parts[0].trim(), parts[1].trim());
        }

        if (commandWord.equalsIgnoreCase("event")) {
            String[] parts = arguments.split("\\s+/from\\s+", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty()) {
                throw new IllegalArgumentException("Usage: event <description> /from <start> /to <end>");
            }
            String[] times = parts[1].split("\\s+/to\\s+", 2);
            if (times.length != 2 || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
                throw new IllegalArgumentException("Usage: event <description> /from <start> /to <end>");
            }
            return new Event(parts[0].trim(), times[0].trim(), times[1].trim());
        }

        throw new IllegalArgumentException("Unknown command. Use todo, deadline, event, list, mark, unmark, or bye.");
    }
}
