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
        String[] tasks = new String[MAX_TASKS];
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
                        System.out.println((i + 1) + ". " + tasks[i]);
                    }
                }
                continue;
            }

            if (taskCount == MAX_TASKS) {
                //improvements to guard against >100 inputs
                System.out.println("I cannot store more than " + MAX_TASKS + " tasks.");
                continue;
            }

            tasks[taskCount] = input;
            taskCount++;
            System.out.println("Added: " + input);
        }
        scanner.close();
    }

}
