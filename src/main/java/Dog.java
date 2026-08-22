import java.util.Scanner;

/** Entry point for the Dog chatbot. */
public class Dog {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String banner = " ____              \n"
                + "|  _ \\  ___   __ _ \n"
                + "| | | |/ _ \\ / _` |\n"
                + "| |_| | (_) | (_| |\n"
                + "|____/ \\___/ \\__, |\n"
                + "             |___/ \n";
        System.out.println(banner);
        System.out.println("Woof! What can I do for you today?");

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
                if (taskCount == 0) {
                    System.out.println("Your task list is empty :(");
                } else {
                    System.out.println("Woof list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                }
                continue;
            }

            String[] commandParts = command.split("\\s+", 2);
            String commandWord = commandParts[0];
            if (commandWord.equalsIgnoreCase("mark") || commandWord.equalsIgnoreCase("unmark")) {
                if (commandParts.length < 2) {
                    System.out.println("Please provide a number within the list range"
                            + commandWord.toLowerCase() + " 1");
                    continue;
                }

                int taskIndex;
                try {
                    taskIndex = Integer.parseInt(commandParts[1]) - 1;
                } catch (NumberFormatException e) {
                    System.out.println("Please provide a valid task number.");
                    //agn guarding from ai improvements
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
                String action = shouldMarkAsDone ? "marked as done" : "marked as not done";
                System.out.println("Task " + (taskIndex + 1) + " has been " + action + ":");
                System.out.println(tasks[taskIndex]);
                continue;
            }

            if (taskCount == MAX_TASKS) {
                //improvements to guard against >100 inputs
                System.out.println("I cannot store more than " + MAX_TASKS + " tasks.");
                continue;
            }

            tasks[taskCount] = new Task(input);
            taskCount++;
            System.out.println("Added: " + input);
        }
        scanner.close();
    }

}
