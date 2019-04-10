package Game;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Drawable - either a sprite or a group of sprites
 * A drawable can be added to stage(the list of things shown on screen) using addToStage or initialized in the
 * stage useing create[drawablename] instead of getNew[drawablename]
 * Some of the drawables implements PosUpdateable interface which allows using updatePosition method on them
 * and to group them into posUpdateableGroup.
 */

public class View {
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
    public Async<Texture> loadTexture(String path) {
        return this.rend.loadTexture(path);
    }
    Async<Texture> loadTexture(Texture.ArrayAndSize params) {
        return this.rend.loadTexture(params);
    }

    /** Removes a texture from GPUs memory, call this if the texture is no longer in use */
    public void unloadTexture(Async<Texture> texture) {
        this.rend.unloadTexture(texture);
    }

    /**
     * Creates and adds to stage a non-moving object, useful for menus and GUIs
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     * Can be deleted by calling deleteDrawable
     */
    Async<Integer> createStaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
        return addToStage(getNewStaticTexturedRectangle(left, right, top, bottom, z_index, texture));
    }

    /**
     * Creates and adds to stage a new texturedRectangle
     * Implements PosUpdateable
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     * Can be deleted by calling deleteDrawable
     */
    public Async<Integer> createTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long currentTimeNanos) {
        return this.addToStage(this.getNewTexturedRectangle(left, right, top, bottom, z_index, texture, translation, velocity, currentTimeNanos));
    }

    /**
     * Creates and adds to stage a background
     * Implements PosUpdateable
     * The background is an object which stretches over the whole rendering context and has a recurring texture
     * Updating position cheanges the texture, not the object
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     */
    public Async<Integer> createBackground(float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp, float aspectRatio) {
        return this.addToStage(this.getNewBackground(z_index, texture, translation, velocity, updatedTimestamp, aspectRatio));
    }

    /**
     * Creates and adds to stage a drawable that is similar to texturedRectangle, but toggles between the left and the right side of a texture enabling two frame animation
     * Implements PosUpdateable
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     * Can be deleted by calling deleteDrawable
     */
    Async<Integer> createAnimatedTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long frameDurationMilis, long updatedTimestamp) {
        return this.addToStage(this.getNewAnimatedTexturedRectangle(left, right, top, bottom, z_index, texture, translation, velocity, frameDurationMilis, updatedTimestamp));
    }

    /**
     * Creates and adds to stage a group of PosUpdateable objects
     * The group is a posupdateable object
     * Only one drawable is shown at a time, to toggle between them use setActiveState method
     */
    Async<Integer> createPosUpdateableGroup(Vector3f translation, Vector3f velocity, HashMap<Integer, Async<Renderer.Drawable>> states, long updatedTimestamp) {
        return this.addToStage(this.getNewPosUpdateableGroup(translation, velocity, states, updatedTimestamp));
    }

    /**
     * Used for updating position of Drawables implementing PosUpdateable
     */
    void updatePosition(Async<Integer> id, Vector3f translation, Vector3f velocity, long timestamp) {
        this.rend.updatePosition(id, translation, velocity, timestamp);
    }

    /**
     * For updating position of multiple Drawables implementing PosUpdateable at once
     */
    void updatePositions(ArrayList<Async<Integer>> ids, Vector3f translation, Vector3f velocity, long timestamp) {
        ids.forEach((id) -> this.updatePosition(id, translation, velocity, timestamp));
    }

    /**
     * Removes a drawable form stage
     */
    public Async<Boolean> deleteDrawable(Async<Integer> id) {
        return this.rend.deleteDrawable(id);
    }

    /**
     * Creates a non-moving object, useful for menus and GUIs
     * Needs to be added to stage to become visible
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     */
    Async<Renderer.Drawable> getNewStaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
        return this.rend.getNewStaticTexturedRectangle(left, right, top, bottom, z_index, texture);
    }

    /**
     * Creates a new texturedRectangle
     * Implements PosUpdateable
     * Needs to be added to stage to become visible
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     */
    Async<Renderer.Drawable> getNewTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long currentTimeNanos) {
        return this.rend.getNewTexturedRectangle(left, right, top, bottom, z_index, texture, translation, velocity, currentTimeNanos);
    }

    /**
     * Creates a background
     * Implements PosUpdateable
     * Needs to be added to stage to become visible
     * The background is an object which stretches over the whole rendering context and has a recurring texture
     * Updating position cheanges the texture, not the object
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     */
    Async<Renderer.Drawable> getNewBackground(float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp, float aspectRatio) {
        return this.rend.getNewBackground(z_index, texture, translation, velocity, updatedTimestamp, aspectRatio);
    }

    /**
     * Creates and adds to stage a drawable that is similar to texturedRectangle, but toggles between the left and the right side of a texture enabling two frame animation
     * Implements PosUpdateable
     * Needs to be added to stage to become visible
     * The z_index has to be BETWEEN 1.0f and -1.0f to be visible, smaller z_index means in front of
     */
    Async<Renderer.Drawable> getNewAnimatedTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long frameDurationMilis, long updatedTimestamp) {
        return this.rend.getNewAnimatedTexturedRectangle(left, right, top, bottom, z_index, texture, translation, velocity, frameDurationMilis, updatedTimestamp);
    }

    /**
     * Creates a group of PosUpdateable objects
     * The group is a posupdateable object
     * Needs to be added to stage to become visible
     * Only one drawable is shown at a time, to toggle between them use setActiveState method
     */
    Async<Renderer.Drawable> getNewPosUpdateableGroup(Vector3f translation, Vector3f velocity, HashMap<Integer, Async<Renderer.Drawable>> states, long updatedTimestamp) {
        return this.rend.getNewPosUpdateableGroup(translation, velocity, states, updatedTimestamp);
    }


    /**
     * Method for toggling between different states of a PosUpdateable Object
     */
    void setActiveState(Async<Integer> id, int state) {
        this.rend.setActiveState(id, state);
    }

    /**
     * Adds a drawable to stage (list of drawables rendered by the renderer)
     * Can be later deleted using deleteDrawable
     */
    Async<Integer> addToStage(Async<Renderer.Drawable> drawable) {
        return this.rend.addToStage(drawable);
    }
}
