import org.lwjgl.glfw.GLFWKeyCallback;

class View {
    private GLFWKeyCallback keyHandler;

    View(GLFWKeyCallback keyHandler) {
        this.keyHandler = keyHandler;
    }

    Renderer rend;

    //creates a new thread and creates a new window and a thread for rendering
    void startRenderer() {
        rend = new Renderer(keyHandler);
        rend.start();
    }

    void setRendererBackgroundColor(float r, float g, float b) {
        rend.setBackgroundColor(r, g, b);
    }

    Texture loadTexture(String path) {
        return rend.loadTexture(path);
    }
}
