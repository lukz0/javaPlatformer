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

        // Ask renderer to load fireFlower texture
        ArrayBlockingQueue<Texture> fireFlowerTexturePromise = v.loadTexture("resources/images/fireFlower.png");
        ArrayBlockingQueue<Texture> marioForwardTexturePromise = v.loadTexture("resources/images/marioForward.png");

        // Block until the renderer returns the fireFlower texture
        Texture fireFlowerTexture = null, marioForwardTexture = null;
        try {
            fireFlowerTexture = fireFlowerTexturePromise.take();
            marioForwardTexture = marioForwardTexturePromise.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ask the renderer to create 4 StaticTexturedRectangles
        ArrayBlockingQueue<Integer> drawableIDPromise = v.createStaticTexturedRectangle(-1.0f, 0.0f, 1.0f, 0.0f, 0, fireFlowerTexture);
        ArrayBlockingQueue<Integer> drawableIDPromise2 = v.createStaticTexturedRectangle(0.0f, 1.0f, 1.0f, 0.0f, 0, marioForwardTexture);
        ArrayBlockingQueue<Integer> drawableIDPromise3 = v.createStaticTexturedRectangle(-1.0f, 0.0f, 0.0f, -1.0f, 0, marioForwardTexture);
        ArrayBlockingQueue<Integer> drawableIDPromise4 = v.createStaticTexturedRectangle(0.0f, 1.0f, 0.0f, -1.0f, 0, fireFlowerTexture);
        int drawableID = 0, drawableID2 = 0, drawableID3 = 0, drawableID4 = 0;

        ArrayBlockingQueue<Integer> marioIDPromise = v.createTexturedRectangle(-1.0f, 1.0f, 1.0f, -1.0f, 0.5f, marioForwardTexture,
                new Renderer.Vector3f(-0.5f, 0, 0), new Renderer.Vector3f(0.0001f, 0, 0), System.currentTimeMillis());

        try {
            System.out.println("[CONTROLLER] Deleting the drawables in 3 seconds");
            Thread.sleep(3000);
            System.out.println("[CONTROLLER] Deleting the drawables...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Block until the renderer returns IDs for the 4 textured rectangles
        try {
            drawableID = drawableIDPromise.take();
            drawableID2 = drawableIDPromise2.take();
            drawableID3 = drawableIDPromise3.take();
            drawableID4 = drawableIDPromise4.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ask the renderer to delete the rectangles
        ArrayList<ArrayBlockingQueue<Boolean>> results = new ArrayList<>();
        results.add(v.deleteDrawable(drawableID));
        results.add(v.deleteDrawable(drawableID2));
        results.add(v.deleteDrawable(drawableID3));
        results.add(v.deleteDrawable(drawableID4));

        // Ask the renderer to remove the fireFlower texture from GPUs memory
        // Don't do this unless you already asked the renderer to delete all the drawables using it
        // Setting the objects to null can help not accidently using the object for new drawables
        v.unloadTexture(fireFlowerTexture);
        //v.unloadTexture(marioForwardTexture);
        fireFlowerTexture = null;
        //marioForwardTexture = null;

        int marioID;
        try {
            marioID = marioIDPromise.take();
            v.updatePosition(marioID, new Renderer.Vector3f(0.5f, 0, 0), new Renderer.Vector3f(-0.0001f, 0, 0), System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Block until the renderer returns booleans saying if the deletion was a success
        for (int i = 1; i <= results.size(); i++) {
            try {
                System.out.println("[CONTROLLER] Deletion of drawable #" + i + " returned: " + results.get(i - 1).take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //TODO: make gameloop contain more logic
        Gameloop gameloop = new Gameloop();
        gameloop.start();

        //TODO: create debugger
        Thread cheater = new Thread(new Cheater(this));
        cheater.start();
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