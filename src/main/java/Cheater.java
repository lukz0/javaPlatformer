import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class Cheater implements Runnable {
    private Gameloop gameloop;
    private Scanner scanner;
    volatile boolean shouldRun;

    Cheater(Gameloop gameloop) {
        this.gameloop = gameloop;
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
        command = command.toLowerCase();
        if (command.startsWith("stop")) {
            gameloop.runCommand(new StopCommand(0));
            //controller.stopGame(0);
        } else if (command.startsWith("set level")) {
            int level = Integer.parseInt(command.substring(10));
            //TODO: create a setlevel function in controller
            System.out.println("[CHEATER] started level " + level);
        } else if (command.startsWith("set mario")) {
            String state = command.substring(10);
            //TODO: have ways to set mario invincible etc
            System.out.println("[CHEATER] set mario state to " + state);

        }
    }

    static abstract class Command {
        abstract void doCommand(Gameloop gameloop);

        ArrayBlockingQueue<Object> callback = new ArrayBlockingQueue<>(1);
    }

    static class StopCommand extends Command {
        private final int status;

        StopCommand(int status) {
            this.status = status;
        }

        @Override
        void doCommand(Gameloop gameloop) {
            gameloop.controller.stopGame(this.status);
        }
    }
}
