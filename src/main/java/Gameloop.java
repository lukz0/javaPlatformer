import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

public class Gameloop implements Runnable {
    final Controller controller;
    final View view;

    Gameloop(Controller controller, View view) {
        this.controller = controller;
        this.view = view;
    }

    static final long TICKDURATION = 20;

    private Thread thread;

    private ArrayBlockingQueue<KeyEvent> keyEventQueue = new ArrayBlockingQueue<>(10000);
    private ArrayBlockingQueue<Cheater.Command> commandQueue = new ArrayBlockingQueue<>(1000);

    public void start() {
        if (thread == null) {
            thread = new Thread(this, "gameloop thread");
            this.thread.start();
        }
    }

    public void run() {
        while (true) {
            long tickStart = System.nanoTime();

            // TODO: game logic
            // Use tickStart as timestamp argument to View methods
            if (!this.keyEventQueue.isEmpty()) {
                ArrayList<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
                do {
                    keyEvents.clear();
                    this.keyEventQueue.drainTo(keyEvents);
                    for (KeyEvent evt : keyEvents) {
                        handleKey(evt.key, evt.action, evt.modifier);
                    }
                } while (!this.keyEventQueue.isEmpty());
            }

            if (!this.commandQueue.isEmpty()) {
                ArrayList<Cheater.Command> commands = new ArrayList<>();
                do {
                    commands.clear();
                    this.commandQueue.drainTo(commands);
                    for (Cheater.Command cmd : commands) {
                        cmd.doCommand(this);
                    }
                } while (!this.commandQueue.isEmpty());
            }

            long tickEnd = System.nanoTime();
            try {
                Thread.sleep(TICKDURATION-((tickEnd-tickStart)/1000000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Async<Object> runCommand(Cheater.Command command) {
        try {
            this.commandQueue.put(command);
            return new Async<>(command.callback);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void keyEvent(KeyEvent evt) {
        try {
            this.keyEventQueue.put(evt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleKey(int key, int action, int modifier) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            this.controller.stopGame(0);
        } else if (key == GLFW_KEY_R && action == GLFW_PRESS) {
            view.setRendererBackgroundColor(1, 0, 0);
        } else if (key == GLFW_KEY_G && action == GLFW_PRESS) {
            view.setRendererBackgroundColor(0, 1, 0);
        } else if (key == GLFW_KEY_B && action == GLFW_PRESS) {
            view.setRendererBackgroundColor(0, 0, 1);
        }
    }
}
