import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

class Controller {
    private View v;


    Controller() {
        v = new View();

        // Creates a window and starts a openGL renderer on a separate thread
        v.startRenderer(new KeyboardHandler(), new WindowCloseHandler());

        //TODO: create debugger
        Thread cheater = new Thread(new Cheater(this));
        cheater.start();

        //TODO: make gameloop contain more logic
        Gameloop gameloop = new Gameloop();
        gameloop.start();

        // Ask renderer to load fireFlower texture
        Async<Texture> fireFlowerTexture = v.loadTexture("resources/images/fireFlower.png");

        // Ask the renderer to create 2 fireFlowers drawables
        Async<Integer> fireFlowerID = v.createStaticTexturedRectangle(-1.0f, 0.0f, 0.0f, -1.0f, 0.1f, fireFlowerTexture);
        Async<Integer> fireFlowerID2 = v.createStaticTexturedRectangle(0.0f, 1.0f, 0.0f, -1.0f, 0.1f, fireFlowerTexture);

        // Ask the renderer to load marios texture
        Async<Texture> marioForwardTexture = v.loadTexture("resources/images/marioForward.png");

        // Ask the renderer to create a mario drawable
        Async<Integer> marioID = v.createTexturedRectangle(-1.0f, 1.0f, 1.0f, -1.0f, 0, marioForwardTexture,
                new Renderer.Vector3f(0, 0, 0), new Renderer.Vector3f(0, 0, 0), System.nanoTime());
        while (true) {
            v.updatePosition(marioID, new Renderer.Vector3f(-0.5f, 0f, 0f), new Renderer.Vector3f(0.02f, 0f, 0f), System.nanoTime());
            try {
                Thread.sleep(50*Gameloop.TICKDURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            v.updatePosition(marioID, new Renderer.Vector3f(0.5f, 0f, 0f), new Renderer.Vector3f(-0.02f, 0f, 0f), System.nanoTime());

            try {
                Thread.sleep(50*Gameloop.TICKDURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class KeyboardHandler extends GLFWKeyCallback {
        @Override
        public void invoke(long window, int key, int scancode, int action, int modifier) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                stopGame(0);
            } else if (key == GLFW_KEY_R && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(1.0f, 0.0f, 0.0f);
            } else if (key == GLFW_KEY_G && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(0.0f, 1.0f, 0.0f);
            } else if (key == GLFW_KEY_B && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(0.0f, 0.0f, 1.0f);
            }
        }
    }

    public class WindowCloseHandler extends GLFWWindowCloseCallback {
        @Override
        public void invoke(long l) {
            stopGame(0);
        }
    }

    public void stopGame(int status) {
        v.stopRenderer();
        System.exit(status);
    }
}