import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

import java.util.concurrent.ArrayBlockingQueue;

class View {
    private Renderer rend;

    /** Creates a window and a renderer on a separate thread */
    void startRenderer(GLFWKeyCallback keyHandler, GLFWWindowCloseCallback closeHandler) {
        this.rend = new Renderer(keyHandler, closeHandler);
        this.rend.start();
    }

    /** Stops renderer and closes the window */
    void stopRenderer() {
        this.rend.shouldRun = false;
    }

    /** Changes the clear color */
    void setRendererBackgroundColor(float r, float g, float b) {
        this.rend.setBackgroundColor(r, g, b);
    }

    /** Creates a texture object and loads the texture itself into GPUs memory
     * Use unloadTexture on the returned Texture to remove it from GPUs memory */
    ArrayBlockingQueue<Texture> loadTexture(String path) {
        return this.rend.loadTexture(path);
    }

    /** Removes a texture from GPUs memory, call this if the texture is no longer in use */
    void unloadTexture(Texture texture) {
        this.rend.unloadTexture(texture);
    }

    /** Creates a non-moving object, useful for menus and GUIs
     * puts an drawableID inte the returned queue after the rectangle is created and assigned an ID
     * The z_index has to be between 1.0f and -1.0f to be visible, smaller z_index means in front of
     * Can be deleted by calling deleteDrawable on the drawableID */
    ArrayBlockingQueue<Integer> createStaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Texture texture) {
        return this.rend.createStaticTexturedRectangle(left, right, top, bottom, z_index, texture);
    }

    ArrayBlockingQueue<Integer> createTexturedRectangle(float left, float right, float top, float bottom, float z_index, Texture texture, Renderer.Vector3f translation, Renderer.Vector3f velocity, long currentTimeMillis) {
        return this.rend.createTexturedRectangle(left, right, top, bottom, z_index, texture, translation, velocity, currentTimeMillis);
    }

    void updatePosition(int id, Renderer.Vector3f translation, Renderer.Vector3f velocity, long timestamp) {
        this.rend.updatePosition(id, translation, velocity, timestamp);
    }

    /** Deletes an drawn object */
    ArrayBlockingQueue<Boolean> deleteDrawable(int id) {
        return this.rend.deleteDrawable(id);
    }
}
