import java.util.Scanner;

public class Cheater implements Runnable {
    private Controller controller;
    private Scanner scanner;
    volatile boolean shouldRun;

    Cheater(Controller controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        this.shouldRun = true;
    }

    @Override
    public void run() {
        String command;
        while (shouldRun) {
            command = scanner.nextLine();
            if (command != null) {
                doCommand(command);
            }
        }
        scanner.close();
    }


    private void doCommand(String command) {
        System.out.println(command);
        if (command.length()<9){return;}
        switch (command.toLowerCase().substring(0, 9)) {
            case ("stop"):
                this.shouldRun = false;
                break;

            case ("set level"):
                int level = Integer.parseInt(command.substring(9));
                //TODO: create a setlevel function in controller
                break;

            case ("set mario"):
                String state = command.substring(9);
                //TODO: have ways to set mario invincible etc
                break;
        }

    }
}
