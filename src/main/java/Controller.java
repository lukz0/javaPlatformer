import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Controller {
    View v;

    Controller() {
        v = new View(this);
        v.startRenderer();
    }

    public class KeyboardHandler extends GLFWKeyCallback {

        @Override
        public void invoke(long window, int key, int scancode, int action, int modifier) {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            } else if ( key == GLFW_KEY_R && action == GLFW_RELEASE ) {
                v.setRendererBackground(1.0f, 0.0f, 0.0f);
            } else if ( key == GLFW_KEY_G && action == GLFW_RELEASE ) {
                v.setRendererBackground(0.0f, 1.0f, 0.0f);
            } else if ( key == GLFW_KEY_B && action == GLFW_RELEASE ) {
                v.setRendererBackground(0.0f, 0.0f, 1.0f);
            }
        }
    }

    public KeyboardHandler defaultKeyHandler = new KeyboardHandler();
}