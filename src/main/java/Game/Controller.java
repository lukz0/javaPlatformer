package Game;

import GUI_Utils.GU_Digit;
import GUI_Utils.GU_Number;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

import java.awt.*;

class Controller {
    private View v;
    private Gameloop gameloop;

    Controller() {
        v = new View();

        // Creates a window and starts a openGL renderer on a separate thread
        v.startRenderer(new KeyboardHandler(), new WindowCloseHandler(), JSONReader.ReadOptions());

        gameloop = new Gameloop(this, this.v);
        gameloop.start();

        Thread cheater = new Thread(new Cheater(this.gameloop));
        cheater.start();
    }

    public class KeyboardHandler extends GLFWKeyCallback {
        @Override
        public void invoke(long window, int key, int scancode, int action, int modifier) {
            if (gameloop != null) {
                gameloop.keyEvent(new KeyEvent(key, action, modifier));
            }
            /*
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                stopGame(0);
            } else if (key == GLFW_KEY_R && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(1.0f, 0.0f, 0.0f);
            } else if (key == GLFW_KEY_G && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(0.0f, 1.0f, 0.0f);
            } else if (key == GLFW_KEY_B && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(0.0f, 0.0f, 1.0f);
            }*/
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