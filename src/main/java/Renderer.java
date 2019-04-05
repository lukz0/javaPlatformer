import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer implements Runnable {
    public boolean shouldRun = true;

    private long window;

    public float background_color_red = 1;
    public float background_color_green = 0;
    public float background_color_blue = 1;

    private Thread t;

    private GLFWKeyCallback keyHandler;
    private GLFWWindowCloseCallback closeHandler;

    public ArrayList<Boolean> usedDrawnElementIDs = new ArrayList<>();
    interface Drawable {
        // If it changes glActiveTexture, it should be reset to GL_TEXTURE_0 at the end
        void draw(long currentTimeStamp);
        void delete();
    }
    interface PosUpdateable extends Drawable {
        void updatePosition(Vector3f translation, Vector3f velocity, long currentTimeNano);
    }
    public HashMap<Integer, Drawable> drawnElements = new HashMap<>();

    static interface Task {
        void doTask(Renderer r);
    }
    ArrayBlockingQueue<Task> taskQueue = new ArrayBlockingQueue<>(1000);

    Renderer(GLFWKeyCallback keyHandler, GLFWWindowCloseCallback closeHandler) {
        this.keyHandler = keyHandler;
        this.closeHandler = closeHandler;
    }

    public void run() {
        System.currentTimeMillis();
        System.out.println("[RENDERER] Using LWJGL version: ".concat(Version.getVersion()));
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        System.out.println("[RENDERER] Exiting");
    }

    void start() {
        if (t == null) {
            t = new Thread(this, "renderer thread");
            t.start();
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        //glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        // Create the window
        window = glfwCreateWindow(1280, 720, "Project Mario!", (Main.FULLSCREEN) ? glfwGetPrimaryMonitor() : NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, this.keyHandler);

        // Setup callback for when user clicks X
        glfwSetWindowCloseCallback(window, this.closeHandler);

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(Main.VSYNC);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        String openGLv = glGetString(GL_VERSION);
        System.out.println("[RENDERER] Using openGL version: ".concat((openGLv == null) ? ("unknown") : (openGLv)));

        this.shaders = new Shaders();

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Set the clear color
        glClearColor(background_color_red, background_color_green, background_color_blue, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (this.shouldRun) {
            if (!this.taskQueue.isEmpty()) {
                Collection<Task> tasksInQueue = new ArrayList<>();
                do {
                    tasksInQueue.clear();
                    this.taskQueue.drainTo(tasksInQueue);
                    for (Task tsk : tasksInQueue) {
                        tsk.doTask(this);
                    }
                } while (!this.taskQueue.isEmpty());
            }
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            long currentTimeStamp = System.nanoTime();
            for (Drawable elem : this.drawnElements.values()) {
                elem.draw(currentTimeStamp);
            }

            int error;
            while ((error = glGetError()) != 0) {
                System.err.println("[RENDERER] OpenGL Error: ".concat(Integer.toString(error)));
            }

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    static class BufferUtils {
        private BufferUtils() {
        }

        public static ByteBuffer createByteBuffer(byte[] array) {
            ByteBuffer result = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
            result.put(array).flip();
            return result;
        }

        public static FloatBuffer createFloatBuffer(float[] array) {
            FloatBuffer result = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            result.put(array).flip();
            return result;
        }

        public static IntBuffer createIntBuffer(int[] array) {
            IntBuffer result = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
            result.put(array).flip();
            return result;
        }
    }

    static class ShaderUtils {
        private ShaderUtils() {
        }

        static class DoubleString {
            String vert;
            String frag;

            DoubleString(String vert, String frag) {
                this.vert = vert;
                this.frag = frag;
            }
        }

        public static int load(String vertPath, String fragPath) {
            DoubleString shaders = Model.loadShaderFiles(vertPath, fragPath);
            return create(shaders);
        }

        public static int create(DoubleString shaders) {
            int program = glCreateProgram();
            int vertID = glCreateShader(GL_VERTEX_SHADER);
            int fragID = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(vertID, shaders.vert);
            glShaderSource(fragID, shaders.frag);

            glCompileShader(vertID);
            if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.print("[RENDERER] Failed to compile vertex shader: ");
                System.err.println(glGetShaderInfoLog(vertID));
                return -1;
            }

            glCompileShader(fragID);
            if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.print("[RENDERER] Failed to compile fragment shader: ");
                System.err.println(glGetShaderInfoLog(fragID));
                return -1;
            }

            glAttachShader(program, vertID);
            glAttachShader(program, fragID);
            glDeleteShader(vertID);
            glDeleteShader(fragID);

            glLinkProgram(program);
            glValidateProgram(program);
            if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
                System.err.print("[RENDERER] Failed to link program: ");
                System.err.println(glGetProgramInfoLog(program));
                return -1;
            }
            return program;
        }
    }

    static class Shaders {
        int active = -1;

        int staticTexturedRectangle;
        int staticTexturedRectangleTexSamplerLoc;
        int staticTexturedRectangleTexSamplerVal = 0;

        int texturedRectangle;
        int texturedRectangleTexSamplerLoc;
        int texturedRectangleTexSamplerVal = 0;
        int texturedRectangleTranslationLoc;

        int background;
        int backgroundTexSamplerLoc;
        int backgroundTexSamplerVal = 0;
        int backgroundTranslationLoc;

        int animatedTexturedRect;
        int animatedTexturedRectTexSamplerLoc;
        int animatedTexturedRectTexSamplerVal = 0;
        int animatedTexturedRectTranslationLoc;
        int animatedTexturedRectFirstFrameLoc;
        boolean animatedTexturedRectFirstFrameVal = false;
        Shaders() {
            this.staticTexturedRectangle = ShaderUtils.load("resources/shaders/staticTexRect/shader.vert", "resources/shaders/staticTexRect/shader.frag");
            this.staticTexturedRectangleTexSamplerLoc = glGetUniformLocation(this.staticTexturedRectangle, "textureSampler");

            this.texturedRectangle = ShaderUtils.load("resources/shaders/TexRect/shader.vert", "resources/shaders/TexRect/shader.frag");
            this.texturedRectangleTexSamplerLoc = glGetUniformLocation(this.texturedRectangle, "textureSampler");
            this.texturedRectangleTranslationLoc = glGetUniformLocation(this.texturedRectangle, "translation");

            this.background = ShaderUtils.load("resources/shaders/background/shader.vert", "resources/shaders/background/shader.frag");
            this.backgroundTexSamplerLoc = glGetUniformLocation(this.background, "textureSampler");
            this.backgroundTranslationLoc = glGetUniformLocation(this.background, "translation");

            this.animatedTexturedRect = ShaderUtils.load("resources/shaders/animatedTexRect/shader.vert", "resources/shaders/animatedTexRect/shader.frag");
            this.animatedTexturedRectTexSamplerLoc = glGetUniformLocation(this.animatedTexturedRect, "textureSampler");
            this.animatedTexturedRectTranslationLoc = glGetUniformLocation(this.animatedTexturedRect, "translation");
            this.animatedTexturedRectFirstFrameLoc = glGetUniformLocation(this.animatedTexturedRect, "firstFrame");
        }

        void activateStaticTexturedRectangle(int texSamplerVal) {
            if (this.active != this.staticTexturedRectangle) {
                glUseProgram(this.staticTexturedRectangle);
                this.active = this.staticTexturedRectangle;
            }
            if (texSamplerVal != this.staticTexturedRectangleTexSamplerVal) {
                glUniform1i(this.staticTexturedRectangleTexSamplerLoc, texSamplerVal);
                this.staticTexturedRectangleTexSamplerVal = texSamplerVal;
            }
        }

        void activateTexturedRectangle(int texSamplerVal, float[] translationVal) {
            if (this.active != this.texturedRectangle) {
                glUseProgram(this.texturedRectangle);
                this.active = this.texturedRectangle;
            }
            if (texSamplerVal != this.texturedRectangleTexSamplerVal) {
                glUniform1i(this.texturedRectangleTexSamplerLoc, texSamplerVal);
                this.texturedRectangleTexSamplerVal = texSamplerVal;
            }
            glUniform3fv(this.texturedRectangleTranslationLoc, translationVal);
        }

        void activateBackground(int texSamplerVal, float[] translationVal) {
            if (this.active != this.background) {
                glUseProgram(this.background);
                this.active = this.background;
            }
            if (texSamplerVal != this.backgroundTexSamplerVal) {
                glUniform1i(this.backgroundTexSamplerLoc, texSamplerVal);
                this.backgroundTexSamplerVal = texSamplerVal;
            }
            glUniform3fv(this.backgroundTranslationLoc, translationVal);
        }

        void activateAnimatedTexturedRect(int texSamplerVal, float[] translationVal, boolean firstFrame) {
            if (this.active != this.animatedTexturedRect) {
                glUseProgram(this.animatedTexturedRect);
                this.active = this.animatedTexturedRect;
            }
            if (texSamplerVal != this.animatedTexturedRectTexSamplerVal) {
                glUniform1i(this.animatedTexturedRectTexSamplerLoc, texSamplerVal);
                this.animatedTexturedRectTexSamplerVal = texSamplerVal;
            }
            glUniform3fv(this.animatedTexturedRectTranslationLoc, translationVal);
            if (firstFrame != this.animatedTexturedRectFirstFrameVal) {
                glUniform1i(this.animatedTexturedRectFirstFrameLoc, (firstFrame) ? 1 : 0);
                this.animatedTexturedRectFirstFrameVal = firstFrame;
            }
        }
    }
    private Shaders shaders;

    class StaticTexturedRectangle implements Drawable {
        // 0 - array_buffer, 1 - index_buffer
        private int[] buffers = new int[] {0, 0};
        private int VAO;
        private Texture texture;

        StaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Texture texture) {
            this.texture = texture;
            int[] VAOs = new int[] {0};
            glGenVertexArrays(VAOs);
            this.VAO = VAOs[0];
            glBindVertexArray(this.VAO);

            float[] positions = {
                    left, top, z_index, 0, 0,
                    left, bottom, z_index, 0, 1,
                    right, bottom, z_index, 1, 1,
                    right, top, z_index, 1, 0};

            int[] indices = {
                    0, 1, 2,
                    0, 2, 3};

            // 0 - array_buffer, 1 - index_buffer
            glGenBuffers(this.buffers);
            glBindBuffer(GL_ARRAY_BUFFER, this.buffers[0]);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*4/*5 floats*/, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5*4, 3*4);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.buffers[1]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        public void draw(long currentTimeStamp) {
            glBindVertexArray(this.VAO);
            shaders.activateStaticTexturedRectangle(0);
            this.texture.bind();
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }

        public void delete() {
            glBindVertexArray(this.VAO);
            glDeleteBuffers(this.buffers);
            glDeleteVertexArrays(this.VAO);
        }
    }

    class TexturedRectangle implements PosUpdateable {
        // 0 - array_buffer, 1 - index_buffer
        private int[] buffers = new int[] {0, 0};
        private int VAO;
        private Texture texture;

        private long updatedTimestamp;
        Vector3f translation;
        Vector3f velocity;

        TexturedRectangle(float left, float right, float top, float bottom, float z_index, Texture texture, Vector3f translation, Vector3f velocity, long updatedTimestamp) {
            this.updatedTimestamp = updatedTimestamp;
            this.translation = translation;
            this.velocity = velocity;
            this.texture = texture;
            this.VAO = glGenVertexArrays();
            glBindVertexArray(this.VAO);
            float[] positions = {
                    left, top, z_index, 0, 0,
                    left, bottom, z_index, 0, 1,
                    right, bottom, z_index, 1, 1,
                    right, top, z_index, 1, 0};

            int[] indices = {
                    0, 1, 2,
                    0, 2, 3};

            // 0 - array_buffer, 1 - index_buffer
            glGenBuffers(this.buffers);
            glBindBuffer(GL_ARRAY_BUFFER, this.buffers[0]);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*4, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5*4, 3*4);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.buffers[1]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        public void draw(long currentTimeStamp) {
            glBindVertexArray(this.VAO);

            long delta = (currentTimeStamp - this.updatedTimestamp)/(1000000*Gameloop.TICKDURATION);
            shaders.activateTexturedRectangle(0, this.translation.add(this.velocity.multiply(delta)).getOpenGLvector());
            this.texture.bind();

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }
        public void delete() {
            glBindVertexArray(this.VAO);
            glDeleteBuffers(this.buffers);
            glDeleteVertexArrays(this.VAO);
        }

        public void updatePosition(Vector3f translation, Vector3f velocity, long currentTimeNano) {
            this.translation = translation;
            this.velocity = velocity;
            this.updatedTimestamp = currentTimeNano;
        }
    }

    class Background implements PosUpdateable {
        // 0 - array_buffer, 1 - index_buffer
        private int[] buffers = new int[] {0, 0};
        private int VAO;
        private Texture texture;

        private long updatedTimestamp;
        Vector3f translation;
        Vector3f velocity;
        Background(float z_index, Texture texture, Vector3f translation, Vector3f velocity, long updatedTimestamp, float aspectRatio) {
            this.texture = texture;
            this.translation = translation;
            this.velocity = velocity;
            this.updatedTimestamp = updatedTimestamp;

            this.VAO = glGenVertexArrays();
            glBindVertexArray(this.VAO);
            // aspectRatio = width/height = 16/9
            // textureRight = 1
            // textureRight = 1/aspectRatio*9/16

            // aspectRatio = width/height = 8/9
            // textureRight = 2
            // textureRight = 1/aspectRatio*9/16

            // aspectRatio = width/height = 32/9
            // textureRight = 0.5
            // textureRight = 1/aspectRatio*9/16
            float textureRight = 1/((aspectRatio*9)/16);
            float[] positions = {
                    -1, 1, z_index, 0, 0,
                    -1, -1, z_index, 0, 1,
                    1, -1, z_index, textureRight, 1,
                    1, 1, z_index, textureRight, 0};
            int[] indices = {
                    0, 1, 2,
                    0, 2, 3};

            glGenBuffers(this.buffers);
            glBindBuffer(GL_ARRAY_BUFFER, this.buffers[0]);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.buffers[1]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*4, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5*4, 3*4);
        }
        public void draw(long currentTimeStamp) {
            glBindVertexArray(this.VAO);

            long delta = (currentTimeStamp - this.updatedTimestamp)/(1000000*Gameloop.TICKDURATION);
            shaders.activateBackground(0, this.translation.add(this.velocity.multiply(delta)).getOpenGLvector());
            this.texture.bind();

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }
        public void delete() {
            glBindVertexArray(this.VAO);
            glDeleteBuffers(this.buffers);
            glDeleteVertexArrays(this.VAO);
        }
        public void updatePosition(Vector3f translation, Vector3f velocity, long currentTimeNano) {
            this.translation = translation;
            this.velocity = velocity;
            this.updatedTimestamp = currentTimeNano;
        }
    }

    class AnimatedTexturedRectangle implements PosUpdateable {
        // 0 - array_buffer, 1 - index_buffer
        private int[] buffers = new int[] {0, 0};
        private int VAO;
        private Texture texture;

        private long updatedTimestamp;
        Vector3f translation;
        Vector3f velocity;

        private long frameDurationMilis;

        AnimatedTexturedRectangle(float left, float right, float top, float bottom, float z_index, Texture texture, Vector3f translation, Vector3f velocity, long frameDurationMilis, long updatedTimestamp) {
            this.updatedTimestamp = updatedTimestamp;
            this.frameDurationMilis = frameDurationMilis;
            this.translation = translation;
            this.velocity = velocity;
            this.texture = texture;
            this.VAO = glGenVertexArrays();
            glBindVertexArray(this.VAO);
            float[] positions = { // x, y, z, frame1s, frame1t, frame2s, frame2t
                    left, top, z_index, 0, 1, 0.5f, 1,
                    left, bottom, z_index, 0, 0, 0.5f, 0,
                    right, bottom, z_index, 0.5f, 0, 1, 0,
                    right, top, z_index, 0.5f, 1, 1, 1};

            int[] indices = {
                    0, 1, 2,
                    0, 2, 3};

            // 0 - array_buffer, 1 - index_buffer
            glGenBuffers(this.buffers);
            glBindBuffer(GL_ARRAY_BUFFER, this.buffers[0]);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 7*4, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 7*4, 3*4);

            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 7*4, 5*4);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.buffers[1]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        public void draw(long currentTimestamp) {
            glBindVertexArray(this.VAO);
            long delta = (currentTimestamp - this.updatedTimestamp)/(1000000*Gameloop.TICKDURATION);
            shaders.activateAnimatedTexturedRect(0, this.translation.add(this.velocity.multiply(delta)).getOpenGLvector(), ((currentTimestamp/1000000)/frameDurationMilis)%2==1);
            this.texture.bind();
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }

        public void delete() {
            glBindVertexArray(this.VAO);
            glDeleteBuffers(this.buffers);
            glDeleteVertexArrays(this.VAO);
        }

        public void updatePosition(Vector3f translation, Vector3f velocity, long currentTimeNano) {
            this.translation = translation;
            this.velocity = velocity;
            this.updatedTimestamp = currentTimeNano;
        }
    }

    static class SetBackgroundColorTask implements Task {
        private float red, green, blue;
        SetBackgroundColorTask(float r, float g, float b) {
            this.red = r;
            this.green = g;
            this.blue = b;
        }
        public void doTask(Renderer r) {
            r.background_color_red = this.red;
            r.background_color_green = this.green;
            r.background_color_blue = this.blue;
            glClearColor(r.background_color_red, r.background_color_green, r.background_color_blue, 1.0f);
        }
    }
    void setBackgroundColor(float r, float g, float b) {
        try {
            this.taskQueue.put(new SetBackgroundColorTask(r, g, b));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class LoadTextureTask implements Task {
        ArrayBlockingQueue<Texture> callbackQueue = new ArrayBlockingQueue<>(1);
        private final String path;

        LoadTextureTask(String path) {
            this.path = path;
        }

        public void doTask(Renderer r) {
            try {
                this.callbackQueue.put(new Texture(this.path));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Texture> loadTexture(String path) {
        LoadTextureTask tsk = new LoadTextureTask(path);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class LoadTextureFromBitmapTask implements Task {
        ArrayBlockingQueue<Texture> callbackQueue = new ArrayBlockingQueue<>(1);
        private final Texture.BitmapAndSize params;

        LoadTextureFromBitmapTask(Texture.BitmapAndSize params) {
            this.params = params;
        }

        public void doTask(Renderer r) {
            try {
                this.callbackQueue.put(new Texture(this.params));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Texture> loadTexture(Texture.BitmapAndSize params) {
        LoadTextureFromBitmapTask tsk = new LoadTextureFromBitmapTask(params);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class UnloadTextureTask implements Task {
        private Async<Texture> texture;
        UnloadTextureTask(Async<Texture> texture) {
            this.texture = texture;
        }
        public void doTask(Renderer r) {
            this.texture.get().unload();
        }
    }
    void unloadTexture(Async<Texture> texture) {
        try {
            this.taskQueue.put(new UnloadTextureTask(texture));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class GetNewStaticTexturedRectangleTask implements Task {
        ArrayBlockingQueue<Drawable> callbackQueue = new ArrayBlockingQueue<>(1);
        private float left, right, top, bottom, z_index;
        private Async<Texture> texture;
        GetNewStaticTexturedRectangleTask(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.z_index = z_index;
            this.texture = texture;
        }
        public void doTask(Renderer r) {
            try {
                this.callbackQueue.put(new StaticTexturedRectangle(this.left, this.right, this.top, this.bottom, this.z_index, this.texture.get()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Drawable> getNewStaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
        GetNewStaticTexturedRectangleTask tsk = new GetNewStaticTexturedRectangleTask(
                convertToOpenGLX(left),
                convertToOpenGLX(right),
                convertToOpenGLY(top),
                convertToOpenGLY(bottom), z_index, texture);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class GetNewTexturedRectangleTask implements Task {
        ArrayBlockingQueue<Drawable> callbackQueue = new ArrayBlockingQueue<>(1);
        private final float left, right, top, bottom, z_index;
        private final Async<Texture> texture;
        private final Vector3f translation, velocity;
        private final long updatedTimestamp;
        GetNewTexturedRectangleTask(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.z_index = z_index;
            this.texture = texture;
            this.translation = translation;
            this.velocity = velocity;
            this.updatedTimestamp = updatedTimestamp;
        }
        public void doTask(Renderer r) {
            try {
                this.callbackQueue.put(new TexturedRectangle(this.left, this.right, this.top, this.bottom, this.z_index, this.texture.get(), this.translation, this.velocity, this.updatedTimestamp));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Drawable> getNewTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp) {
        GetNewTexturedRectangleTask tsk = new GetNewTexturedRectangleTask(
                convertToOpenGLX(left),
                convertToOpenGLX(right),
                convertToOpenGLY(top),
                convertToOpenGLY(bottom),
                z_index, texture, translation, velocity, updatedTimestamp);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class GetNewBackgroundTask implements Task {
        ArrayBlockingQueue<Drawable> callbackQueue = new ArrayBlockingQueue<>(1);
        private final float z_index, aspectRatio;
        private final Async<Texture> texture;
        private final Vector3f translation, velocity;
        private final long updatedTimestamp;
        GetNewBackgroundTask(float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp, float aspectRatio) {
            this.z_index = z_index;
            this.texture = texture;
            this.translation = translation;
            this.velocity = velocity;
            this.updatedTimestamp = updatedTimestamp;
            this.aspectRatio = aspectRatio;
        }
        public void doTask(Renderer r) {
            try {
                this.callbackQueue.put(new Background(this.z_index, this.texture.get(), this.translation, this.velocity, this.updatedTimestamp, this.aspectRatio));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Drawable> getNewBackground(float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long updatedTimestamp, float aspectRatio) {
        GetNewBackgroundTask tsk = new GetNewBackgroundTask(z_index, texture, translation, velocity, updatedTimestamp, aspectRatio);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class GetNewAnimatedTexturedRectangleTask implements Task {
        ArrayBlockingQueue<Drawable> callbackQueue = new ArrayBlockingQueue<>(1);
        private final float left, right, top, bottom, z_index;
        private final Async<Texture> texture;
        private final Vector3f translation, velocity;
        private final long frameDurationMilis, updatedTimestamp;
        GetNewAnimatedTexturedRectangleTask(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long frameDurationMilis, long updatedTimestamp) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.z_index = z_index;
            this.texture = texture;
            this.translation = translation;
            this.velocity = velocity;
            this.frameDurationMilis = frameDurationMilis;
            this.updatedTimestamp = updatedTimestamp;
        }

        public void doTask(Renderer r) {
            try {
                this.callbackQueue.put(new AnimatedTexturedRectangle(this.left, this.right, this.bottom, this.top, this.z_index, this.texture.get(), this.translation, this.velocity, this.frameDurationMilis, this.updatedTimestamp));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Drawable> getNewAnimatedTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long frameDurationMilis, long updatedTimestamp) {
        GetNewAnimatedTexturedRectangleTask tsk = new GetNewAnimatedTexturedRectangleTask(convertToOpenGLX(left), convertToOpenGLX(right),
                convertToOpenGLY(top), convertToOpenGLY(bottom), z_index, texture, translation, velocity, frameDurationMilis, updatedTimestamp);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class AddToStageTask implements Task {
        ArrayBlockingQueue<Integer> callbackQueue = new ArrayBlockingQueue<>(1);
        private final Async<Drawable> drawable;
        AddToStageTask(Async<Drawable> drawable) {
            this.drawable = drawable;
        }
        public void doTask(Renderer r) {
            int unusedIndex = r.usedDrawnElementIDs.indexOf(false);
            int id;
            if (unusedIndex == -1) {
                id = r.usedDrawnElementIDs.size();
                r.usedDrawnElementIDs.add(true);
            } else {
                id = unusedIndex;
                r.usedDrawnElementIDs.set(id, true);
            }
            r.drawnElements.put(id, this.drawable.get());
            try {
                this.callbackQueue.put(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Integer> addToStage(Async<Drawable> drawable) {
        AddToStageTask tsk = new AddToStageTask(drawable);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class UpdatePositionTask implements Task {
        private final Async<Integer> id;
        private final Vector3f translation, velocity;
        private final long timestamp;
        UpdatePositionTask(Async<Integer> id, Vector3f translation, Vector3f velocity, long timestamp) {
            this.id = id;
            this.translation = translation;
            this.velocity = velocity;
            this.timestamp = timestamp;
        }
        public void doTask(Renderer r) {
            ((PosUpdateable)r.drawnElements.get(id.get())).updatePosition(this.translation, this.velocity, this.timestamp);
        }
    }
    void updatePosition(Async<Integer> id, Vector3f translation, Vector3f velocity, long timestamp) {
        UpdatePositionTask tsk = new UpdatePositionTask(id, translation, velocity, timestamp);
        try {
            this.taskQueue.put(tsk);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class DeleteDrawableTask implements Task {
        ArrayBlockingQueue<Boolean> callbackQueue = new ArrayBlockingQueue<>(1);
        private Async<Integer> id;
        DeleteDrawableTask(Async<Integer> id) {
            this.id = id;
        }
        public void doTask(Renderer r) {
            r.usedDrawnElementIDs.set(this.id.get(), false);
            r.drawnElements.get(this.id.get()).delete();
            try {
                callbackQueue.put(r.drawnElements.remove(this.id.get()) != null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Boolean> deleteDrawable(Async<Integer> id) {
        DeleteDrawableTask tsk = new DeleteDrawableTask(id);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private float convertToOpenGLX(float x) {
        return ((x*2f)/16f)-1f;
    }
    private float convertToOpenGLY(float y) {
        return ((y*2f)/9f)-1f;
    }
}