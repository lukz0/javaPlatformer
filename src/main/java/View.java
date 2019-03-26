import org.lwjgl.glfw.GLFWKeyCallback;

public class View {
    private GLFWKeyCallback keyHandler;

    View(GLFWKeyCallback keyHandler) {
        this.keyHandler = keyHandler;
    }

    Renderer rend;

    public void startRenderer() {
        rend = new Renderer(keyHandler);
        rend.start();
    }

    void setRendererBackgroundColor(float r, float g, float b) {
        rend.setBackgroundColor(r, g, b);
    }
}
