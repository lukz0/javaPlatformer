package Game;

import GUI_Utils.GU_Digit;
import GUI_Utils.GU_Number;
import Level.Block.AbstractBlock;
import Level.Block.StaticAbstractBlock;
import Level.Chunk;
import Level.Level;
import Level.Tilemap;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

public class Gameloop implements Runnable {
    public static final float WORLD_LAYER = 0f;
    public static final float SKY_LAYER = 0.9f;

    public final Controller controller;
    public final View view;

    public int score;

    HashMap<String, Async<Texture>> textures = new HashMap<>();

    Gameloop(Controller controller, View view) {
        this.controller = controller;
        this.view = view;
    }

    public static final long TICKDURATION = 20;

    private Thread thread;

    private ArrayBlockingQueue<KeyEvent> keyEventQueue = new ArrayBlockingQueue<>(10000);
    private ArrayBlockingQueue<Cheater.Command> commandQueue = new ArrayBlockingQueue<>(1000);

    public boolean holdingLeft = false;
    public boolean holdingRight = false;
    public boolean holdingUp = false;
    public boolean holdingDown = false;
    public boolean holdingEnter = false;
    boolean holdingSpace = false;

    public void start() {
        if (thread == null) {
            thread = new Thread(this, "gameloop thread");
            this.thread.start();
        }
    }

    Level level;

    private boolean isPaused = false;
    PauseMenu.Main pm;
    public void enterPause() {
        if (!this.isPaused) {
            this.isPaused = true;
            this.pm = new PauseMenu.Main(this.view, this);
            this.level.pause(this.view);
        }
    }

    public void exitPause() {
        if (this.isPaused) {
            this.isPaused = false;
            this.pm.deleteMenu();
            this.level.unPause(this.view, System.nanoTime());
            this.pm = null;
        }
    }

    public void run() {

        //Level lvl = loadChunk();
        long tickStart = System.nanoTime();
        this.level = JSONReader.ReadLevel("resources/maps/plain.json").loadLevel(this.view,tickStart);
        this.score = 420;
        GU_Number gun = new GU_Number(this.view, new TextCreator((int)(200* GU_Digit.KERNING), 200, Color.BLACK), 5, 1, -0.9f,
                new Vector3f(13.5f, 8, 0));

        while (true) {
            runKeyEventQueue();
            runCommandQueue();

            if (this.isPaused) {
                this.pm.tick(this);
            } else {
                this.level.doPhysics(this, tickStart, this.view);
            }

            gun.setNumber(this.view, score);

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
        Level.LevelBackground background1 = new Level.LevelBackground("resources/images/backgrounds/plainsSky.png", 64f/288f, Gameloop.SKY_LAYER, 0, new Vector3f(0.01f, 0, 0));
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
            this.enterPause();
        } else if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_SPACE:
                    this.holdingSpace = true;
                    break;
                case GLFW_KEY_LEFT:
                    this.holdingLeft = true;
                    break;
                case GLFW_KEY_RIGHT:
                    this.holdingRight = true;
                    break;
                case GLFW_KEY_UP:
                    this.holdingUp = true;
                    break;
                case GLFW_KEY_DOWN:
                    this.holdingDown = true;
                    break;
                case GLFW_KEY_ENTER:
                    this.holdingEnter = true;
                    break;
            }
        } else if (action == GLFW_RELEASE) {
            switch (key) {
                case GLFW_KEY_SPACE:
                    this.holdingSpace = false;
                    break;
                case GLFW_KEY_LEFT:
                    this.holdingLeft = false;
                    break;
                case GLFW_KEY_RIGHT:
                    this.holdingRight = false;
                    break;
                case GLFW_KEY_UP:
                    this.holdingUp = false;
                    break;
                case GLFW_KEY_DOWN:
                    this.holdingDown = false;
                    break;
                case GLFW_KEY_ENTER:
                    this.holdingEnter = false;
                    break;
            }
        }
    }

    public void stopGame(int i) {
        this.controller.stopGame(i);
    }
}
