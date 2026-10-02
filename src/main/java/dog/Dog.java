package dog;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import dog.parser.Parser;
import dog.storage.Storage;
import dog.task.Task;

/**
 * Runs the Dog chatbot and handles commands entered by the user.
 */
public class Dog {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments supplied to the program.
     */
    public static void main(String[] args) {
        printGreeting();

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(Path.of("data", "dog.txt"));
        ArrayList<Task> tasks = loadTasks(storage);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String command = input.trim();

            if (command.equalsIgnoreCase("bye")) {
                System.out.println("Woof! See you again!");
                break;
            }

            try {
                executeCommand(command, tasks, storage);
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            } catch (IOException e) {
                printError("OOPS!!! I couldn't save your tasks.");
            }
        }
        scanner.close();
    }

    /**
     * Executes a non-exit command.
     * Invalid commands leave the task list unchanged.
     */
    private static void executeCommand(String command, ArrayList<Task> tasks, Storage storage)
            throws IOException {
        if (command.equalsIgnoreCase("list")) {
            printTaskList(tasks);
            return;
        }

        String[] commandParts = command.split("\\s+", 2);
        String commandWord = commandParts[0];
        String arguments = commandParts.length == 2 ? commandParts[1] : "";
        if (commandWord.equalsIgnoreCase("find")) {
            printMatchingTasks(arguments.trim(), tasks);
            return;
        }
        if (commandWord.equalsIgnoreCase("mark") || commandWord.equalsIgnoreCase("unmark")) {
            updateTaskStatus(commandWord, arguments, tasks);
            storage.saveTasks(tasks);
            return;
        }
        if (commandWord.equalsIgnoreCase("delete")) {
            deleteTask(arguments, tasks);
            storage.saveTasks(tasks);
            return;
        }

        Task task = Parser.createTask(commandWord, arguments.trim());
        tasks.add(task);
        storage.saveTasks(tasks);
        printAddedTask(task, tasks.size());
    }

    private static ArrayList<Task> loadTasks(Storage storage) {
        try {
            ArrayList<Task> tasks = storage.loadTasks();
            if (storage.getSkippedLineCount() > 0) {
                int skippedLineCount = storage.getSkippedLineCount();
                String entryLabel = skippedLineCount == 1 ? "entry" : "entries";
                printError("OOPS!!! I skipped " + skippedLineCount + " corrupted data " + entryLabel + ".");
            }
            return tasks;
        } catch (IOException e) {
            printError("OOPS!!! I couldn't load your saved tasks.");
            return new ArrayList<>();
        }
    }

    /**
     * Deletes a selected task from the list.
     */
    private static void deleteTask(String arguments, ArrayList<Task> tasks) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Please provide a task number, for example: delete 1");
        }
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
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
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
        boolean shouldMarkAsDone = commandWord.equalsIgnoreCase("mark");
        if (shouldMarkAsDone) {
            tasks.get(taskIndex).markAsDone();
        } else {
            tasks.get(taskIndex).markAsNotDone();
        }
        printTaskStatus(tasks.get(taskIndex), taskIndex + 1, shouldMarkAsDone);
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

    /**
     * Displays description matches in list order without changing the stored tasks.
     * Result numbers are local to the search results.
     */
    private static void printMatchingTasks(String keyword, ArrayList<Task> tasks) {
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("Please provide a keyword, for example: find book");
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        System.out.println(DIVIDER);
        System.out.println("Here are the matching tasks in your list:");
        int matchCount = 0;
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchCount++;
                System.out.println(matchCount + "." + task);
            }
        }
        if (matchCount == 0) {
            System.out.println("No matching tasks found.");
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

}
