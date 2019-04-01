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
        //allows the command function to use strings shorter than 9 characters long
        String substring = command.length()<10?command.toLowerCase():command.toLowerCase().substring(0,10);
        switch (substring) {
            case ("stop"):
                controller.stopGame(0);
                break;

            case ("set level "):
                int level = Integer.parseInt(command.substring(10));
                //TODO: create a setlevel function in controller
                System.out.println("[CHEATER] started level "+level);
                break;

            case ("set mario "):
                String state = command.substring(10);
                //TODO: have ways to set mario invincible etc
                System.out.println("[CHEATER] set mario state to "+state);
                break;
        }

    }
}
