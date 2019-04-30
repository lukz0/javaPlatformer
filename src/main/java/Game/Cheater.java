package Game;

import Level.Level;

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
                doCommand(command.trim());
            }
        }
        scanner.close();
    }


    private void doCommand(String command) {
        command = command.toLowerCase();

        if (command.startsWith("stop")) {
            gameloop.runCommand(new StopCommand(0));
        } else if (command.startsWith("set level")) {
            String levelpath = command.substring(10);
            //TODO: create a setlevel function in controller
            System.out.println("[CHEATER] started level " + levelpath);
            gameloop.runCommand(new LevelCommand(JSONReader.ReadLevel(levelpath)));
        } else if (command.startsWith("set mario")) {
            String state = command.substring(10);
            //TODO: have ways to set mario invincible etc
            System.out.println("[CHEATER] set mario state to " + state);

        } else if (command.startsWith("set score ")) {
            String s_score = command.substring(10);
            try {
                int score = Integer.valueOf(s_score);
                if (score >= 0) {
                    gameloop.runCommand(new SetScoreCommand(score));
                } else {
                    System.out.println("Please enter a positive number");
                }
            } catch (Exception e) {
                System.out.println("Not a valid integer");
            }
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

    static class LevelCommand extends Command {
        private final Level level;

        LevelCommand(Level level) {
            this.level = level;
        }

        @Override
        void doCommand(Gameloop gameloop) {
            //gameloop.controller;
            return;
        }
    }

    static class SetScoreCommand extends Command {
        private final int score;
        SetScoreCommand(int score) {
            this.score = score;
        }
        void doCommand(Gameloop gameloop) {
            gameloop.score = this.score;
        }
    }
}
