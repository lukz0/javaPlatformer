import Level.Block.AbstractBlock;
import Level.Block.StaticAbstractBlock;
import Level.Chunk;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

public class Gameloop implements Runnable {
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
        Mario mario = new Mario(this.view, this.textures, System.nanoTime());
        Goomba goomba = new Goomba(this.view, this.textures, System.nanoTime());

        Async<Texture> fireFlowerTexture = view.loadTexture("resources/images/fireFlower.png");
        Async<Integer> fireFlower1ID = view.createStaticTexturedRectangle(0f, 1.0f, 1.0f, 0f, 0.1f, fireFlowerTexture);
        Async<Integer> fireFlower2ID = view.createStaticTexturedRectangle(15.0f, 16.0f, 1.0f, 0f, 0.1f, fireFlowerTexture);

        loadChunk();

        while (true) {
            long tickStart = System.nanoTime();
            runKeyEventQueue();
            runCommandQueue();

            // TODO: game logic
            // Use tickStart as timestamp argument to View methods

            mario.doMove(this, tickStart);
            goomba.doMove(this, tickStart);

            long tickEnd = System.nanoTime();
            try {
                long sleepDur = TICKDURATION-((tickEnd-tickStart)/1000000);
                sleepDur = (sleepDur < 0) ? 0 : sleepDur;
                Thread.sleep(sleepDur);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void loadChunk() {
        Chunk cnk = new Chunk();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                AbstractBlock block = cnk.blockList.get(y).get(x);
                if (block != null) {
                    System.out.println(block.isStatic);
                    if (block.isStatic) {
                        Async<Texture> texture;
                        String texturePath = ((StaticAbstractBlock)block).texturePath;
                        if (this.textures.containsKey(texturePath)) {
                            texture = this.textures.get(texturePath);
                        } else {
                            texture = this.view.loadTexture(texturePath);
                            this.textures.put(texturePath, texture);
                        }
                        view.createTexturedRectangle(x, x+1, y+1, y, 0, texture,
                                Vector3f.EMPTY, Vector3f.EMPTY, System.nanoTime());
                    }
                }
            }
        }
    }

    void runKeyEventQueue() {
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
    }
    void runCommandQueue() {
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
