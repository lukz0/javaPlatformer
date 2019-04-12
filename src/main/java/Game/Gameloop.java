package Game;

import Level.Block.AbstractBlock;
import Level.Block.StaticAbstractBlock;
import Level.Chunk;
import Level.Level;
import Level.Tilemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

public class Gameloop implements Runnable {
    public static final float WORLD_LAYER = 0f;
    public static final float SKY_LAYER = 0.9f;

    final Controller controller;
    final View view;

    HashMap<String, Async<Texture>> textures = new HashMap<>();

    Gameloop(Controller controller, View view) {
        this.controller = controller;
        this.view = view;
    }

    static final long TICKDURATION = 20;

    private Thread thread;

    private ArrayBlockingQueue<KeyEvent> keyEventQueue = new ArrayBlockingQueue<>(10000);
    private ArrayBlockingQueue<Cheater.Command> commandQueue = new ArrayBlockingQueue<>(1000);

    public boolean holdingLeft = false;
    public boolean holdingRight = false;

    public void start() {
        if (thread == null) {
            thread = new Thread(this, "gameloop thread");
            this.thread.start();
        }
    }

    public void run() {

        Level lvl = loadChunk();

        long tickStart = System.nanoTime();
        while (true) {
            runKeyEventQueue();
            runCommandQueue();

            // TODO: game logic
            // Use tickStart as timestamp argument to View methods

            lvl.doPhysics(this, tickStart);

            long tickEnd = System.nanoTime();
            try {
                long sleepDur = TICKDURATION*1000000-(tickEnd-tickStart);
                if (sleepDur < 0) {
                    sleepDur = 0;
                }
                TimeUnit.NANOSECONDS.sleep(sleepDur);
                tickStart = tickEnd+sleepDur;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: add a better way to load Levels
    Level loadChunk() {
        Chunk cnk = new Chunk();
        ArrayList<String> blockList = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                blockList.add((y == 0) ? "Ground2" : null);
            }
        }
        cnk.loadStringList(blockList.stream().toArray(String[]::new));
        ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
        chunkList.add(cnk);
        Level.LevelBackground background1 = new Level.LevelBackground("resources/images/backgrounds/plainsSky.png", 64f/288f, Gameloop.SKY_LAYER);
        ArrayList<Level.LevelBackground> backgrounds = new ArrayList<>();
        backgrounds.add(background1);
        Level lvl = new Level(backgrounds, new Tilemap(2, Temporary.Temporary.doubleGroundStringArray()));
        lvl.loadLevel(this.view, System.nanoTime());
        return lvl;
    }

    void runKeyEventQueue() {
        if (!this.keyEventQueue.isEmpty()) {
            ArrayList<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
            do {
                keyEvents.clear();
                this.keyEventQueue.drainTo(keyEvents);
                keyEvents.forEach((evt) -> handleKey(evt.key, evt.action, evt.modifier));
            } while (!this.keyEventQueue.isEmpty());
        }
    }
    void runCommandQueue() {
        if (!this.commandQueue.isEmpty()) {
            ArrayList<Cheater.Command> commands = new ArrayList<>();
            do {
                commands.clear();
                this.commandQueue.drainTo(commands);
                commands.forEach((cmd) -> cmd.doCommand(this));
            } while (!this.commandQueue.isEmpty());
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
        } else if (key == GLFW_KEY_LEFT && action == GLFW_PRESS) {
            this.holdingLeft = true;
        } else if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
            this.holdingLeft = false;
        } else if (key == GLFW_KEY_RIGHT && action == GLFW_PRESS) {
            this.holdingRight = true;
        } else if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
            this.holdingRight = false;
        }
    }
}
