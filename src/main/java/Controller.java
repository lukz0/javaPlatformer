import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

class Controller {
    private View v;

    Controller() {
        v = new View(new KeyboardHandler());
        // starts renderer on a separate thread
        v.startRenderer();

        System.out.println(v.loadTexture("resources/images/fireFlower.png"));
        //TODO: create debugger
        Thread cheater = new Thread(new Cheater(this));
        cheater.start();
    }

    public class KeyboardHandler extends GLFWKeyCallback {

        @Override
        public void invoke(long window, int key, int scancode, int action, int modifier) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            } else if (key == GLFW_KEY_R && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(1.0f, 0.0f, 0.0f);
            } else if (key == GLFW_KEY_G && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(0.0f, 1.0f, 0.0f);
            } else if (key == GLFW_KEY_B && action == GLFW_PRESS) {
                v.setRendererBackgroundColor(0.0f, 0.0f, 1.0f);
            }
        }
    }
}