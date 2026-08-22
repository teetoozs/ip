import java.util.Scanner;

/** Entry point for the Dog chatbot. */
public class Dog {
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
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("bye")) {
                System.out.println("Woof! See you again!");
                break;
            }
            System.out.println(input);
        }
        scanner.close();
    }

}
