import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

import java.util.concurrent.ArrayBlockingQueue;

class View {
    private Renderer rend;

    /**
     * Creates a window and a renderer on a separate thread
     */
    void startRenderer(GLFWKeyCallback keyHandler, GLFWWindowCloseCallback closeHandler) {
        this.rend = new Renderer(keyHandler, closeHandler);
        this.rend.start();
    }

    /**
     * Stops renderer and closes the window
     */
    void stopRenderer() {
        this.rend.shouldRun = false;
    }

    /**
     * Changes the clear color
     */
    void setRendererBackgroundColor(float r, float g, float b) {
        this.rend.setBackgroundColor(r, g, b);
    }

    /** Creates a texture object and loads the texture itself into GPUs memory
     * Use unloadTexture on the returned Texture to remove it from GPUs memory */
    Async<Texture> loadTexture(String path) {
        return this.rend.loadTexture(path);
    }

    /** Removes a texture from GPUs memory, call this if the texture is no longer in use */
    void unloadTexture(Async<Texture> texture) {
        this.rend.unloadTexture(texture);
    }

    /**
     * Creates a non-moving object, useful for menus and GUIs
     * puts an drawableID inte the returned queue after the rectangle is created and assigned an ID
     * The z_index has to be between 1.0f and -1.0f to be visible, smaller z_index means in front of
     * Can be deleted by calling deleteDrawable on the drawableID
     */
    Async<Integer> createStaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
        return addToStage(getNewStaticTexturedRectangle(left, right, top, bottom, z_index, texture));
    }

    /**
     * Creates a moving drawable, position can be changed by calling updatePosition
     * @param translation The position relative to the one specified by left, right, top, bottom and z_index parameters
     * @param velocity The speed the object moves at /tick
     * @param currentTimeNanos The time the current tick began at in nanoseconds
     * @return A drawableID
     */
    Async<Integer> createTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long currentTimeNanos) {
        return addToStage(getNewTexturedRectangle(left, right, top, bottom, z_index, texture, translation, velocity, currentTimeNanos));
    }

    Async<Integer> createBackground(float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp, float aspectRatio) {
        return this.addToStage(this.getNewBackground(z_index, texture, translation, velocity, updatedTimestamp, aspectRatio));
    }

    /**
     * Used for updating position of moving drawables
     * @param timestamp System.nanoTime() at the start of the tick
     */
    void updatePosition(Async<Integer> id, Vector3f translation, Vector3f velocity, long timestamp) {
        this.rend.updatePosition(id, translation, velocity, timestamp);
    }

    /** Deletes an drawn object */
    Async<Boolean> deleteDrawable(Async<Integer> id) {
        return this.rend.deleteDrawable(id);
    }

    Async<Renderer.Drawable> getNewStaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
        return this.rend.getNewStaticTexturedRectangle(left, right, top, bottom, z_index, texture);
    }

    Async<Renderer.Drawable> getNewTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long currentTimeNanos) {
        return this.rend.getNewTexturedRectangle(left, right, top, bottom, z_index, texture, translation, velocity, currentTimeNanos);
    }

    Async<Renderer.Drawable> getNewBackground(float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp, float aspectRatio) {
        return this.rend.getNewBackground(z_index, texture, translation, velocity, updatedTimestamp, aspectRatio);
    }

    Async<Integer> addToStage(Async<Renderer.Drawable> drawable) {
        return this.rend.addToStage(drawable);
    }
}
