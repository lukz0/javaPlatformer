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
    interface PosUpdateable {
        void updatePosition(Vector3f translation, Vector3f velocity, long currentTimeMillis);
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
        window = glfwCreateWindow(800, 800, "Project Mario!", NULL, NULL);
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
        glfwSwapInterval(1);

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

    static class Vector3f {
        float[] values;

        Vector3f(float x, float y, float z) {
            this.values = new float[]{x, y, z};
        }
    }

    static class Matrix4f {
        float[] values;

        // v(row)(column)
        Matrix4f(float v11, float v21, float v31, float v41,
                 float v12, float v22, float v32, float v42,
                 float v13, float v23, float v33, float v43,
                 float v14, float v24, float v34, float v44) {
            this.values = new float[]{
                    v11, v21, v31, v41,
                    v12, v22, v32, v42,
                    v13, v23, v33, v43,
                    v14, v24, v34, v44};
        }

        public static Matrix4f createIdentity() {
            return new Matrix4f(
                    1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            );
        }

        public static Matrix4f createOrtographic(float left, float right, float bottom, float top, float near, float far) {
            /*Matrix4f result = createIdentity();

            //14
            result.values[0 + 3*4] = -(left + right)/2; // center x
            //24
            result.values[1 + 3*4] = -(top + bottom)/2; // center y
            //34
            result.values[2 + 3*4] = -(near + far)/2; // center z

            //11
            result.values[0 + 0*4] = 2.0f / (right - left); // scale x
            //22
            result.values[1 + 1*4] = 2.0f / (top - bottom); // scale y
            //33
            result.values[2 + 2*4] = 2.0f / (far - near); // scale z

            return result;*/
            // The code above is a more verbose way of doing this:
            return new Matrix4f(
                    2.0f / (right - left), 0, 0, 0,
                    0, 2.0f / (top - bottom), 0, 0,
                    0, 0, 2.0f / (far - near), 0,
                    -(left + right) / 2.0f, -(top + bottom) / 2.0f, -(near + far) / 2.0f, 1
            );
        }

        public static Matrix4f createTranslate(Vector3f vector) {
            return new Matrix4f(
                    1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0,
                    vector.values[0], vector.values[1], vector.values[2], 1
            );
        }

        public Matrix4f multiply(Matrix4f matrix) {
            return new Matrix4f(
                    // first row this, first column matrix
                    this.values[0 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 0 * 4],
                    // second row this, first column matrix
                    this.values[1 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 0 * 4],
                    // third row this, first column matrix
                    this.values[2 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 0 * 4],
                    // fourth row this, first column matrix
                    this.values[3 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 0 * 4],
                    //
                    // first row this, second column matrix
                    this.values[0 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 1 * 4],
                    // second row this, second column matrix
                    this.values[1 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 1 * 4],
                    // third row this, second column matrix
                    this.values[2 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 1 * 4],
                    // fourth row this, second column matrix
                    this.values[3 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 1 * 4],
                    //
                    // first row this, third column matrix
                    this.values[0 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 2 * 4],
                    // second row this, third column matrix
                    this.values[1 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 2 * 4],
                    // third row this, third column matrix
                    this.values[2 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 2 * 4],
                    // fourth row this, third column matrix
                    this.values[3 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 2 * 4],
                    //
                    // first row this, fourth column matrix
                    this.values[0 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 3 * 4],
                    // second row this, fourth column matrix
                    this.values[1 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 3 * 4],
                    // third row this, fourth column matrix
                    this.values[2 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 3 * 4],
                    // fourth row this, fourth column matrix
                    this.values[3 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 3 * 4]
            );
        }

        public FloatBuffer toFloatBuffer() {
            return BufferUtils.createFloatBuffer(this.values);
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

    static class StaticTexturedRectangle implements Drawable {
        // 0 - array_buffer, 1 - index_buffer
        private int[] buffers = new int[] {0, 0};
        private int program;
        private int VAO;
        private Texture texture;

        private int textureSamplerLocation;

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

            this.program = ShaderUtils.load("resources/shaders/staticTexRect/shader.vert", "resources/shaders/staticTexRect/shader.frag");
            glUseProgram(this.program);
            this.textureSamplerLocation = glGetUniformLocation(this.program, "textureSampler");
        }

        public void draw(long currentTimeStamp) {
            glBindVertexArray(this.VAO);
            glUseProgram(this.program);
            glUniform1i(this.textureSamplerLocation, 0);
            this.texture.bind();
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }

        public void delete() {
            glBindVertexArray(this.VAO);
            glDeleteBuffers(this.buffers);
            glDeleteProgram(this.program);
            glDeleteVertexArrays(this.VAO);
        }
    }

    static class TexturedRectangle implements Drawable, PosUpdateable {
        // 0 - array_buffer, 1 - index_buffer
        private int[] buffers = new int[] {0, 0};
        private int program;
        private int VAO;
        private Texture texture;

        private int textureSamplerLocation;
        private int translationLocation;

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
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*4/*5 floats*/, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5*4, 3*4);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.buffers[1]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            this.program = ShaderUtils.load("resources/shaders/texRect/shader.vert", "resources/shaders/texRect/shader.frag");
            glUseProgram(this.program);
            this.textureSamplerLocation = glGetUniformLocation(this.program, "textureSampler");
            this.translationLocation = glGetUniformLocation(this.program, "translation");
        }

        public void draw(long currentTimeStamp) {
            glBindVertexArray(this.VAO);
            glUseProgram(this.program);

            glUniform1i(this.textureSamplerLocation, 0);
            this.texture.bind();

            long delta = ((currentTimeStamp - this.updatedTimestamp)/1000000)/Controller.tickDuration;
            glUniform3f(this.translationLocation,
                    this.translation.values[0]+this.velocity.values[0]*delta,
                    this.translation.values[1]+this.velocity.values[1]*delta,
                    this.translation.values[2]+this.velocity.values[2]*delta);

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }
        public void delete() {
            glBindVertexArray(this.VAO);
            glDeleteBuffers(this.buffers);
            glDeleteProgram(this.program);
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

    static class CreateStaticTexturedRectangleTask implements Task {
        ArrayBlockingQueue<Integer> callbackQueue = new ArrayBlockingQueue<>(1);
        private float left, right, top, bottom, z_index;
        private Async<Texture> texture;
        CreateStaticTexturedRectangleTask(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.z_index = z_index;
            this.texture = texture;
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
            r.drawnElements.put(id, new StaticTexturedRectangle(this.left, this.right, this.top, this.bottom, this.z_index, this.texture.get()));

            try {
                this.callbackQueue.put(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Integer> createStaticTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture) {
        CreateStaticTexturedRectangleTask tsk = new CreateStaticTexturedRectangleTask(left, right, top, bottom, z_index, texture);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class CreateTexturedRectangleTask implements Task {
        ArrayBlockingQueue<Integer> callbackQueue = new ArrayBlockingQueue<>(1);
        private float left, right, top, bottom, z_index;
        private Async<Texture> texture;
        private Vector3f translation, velocity;
        private long timestamp;
        CreateTexturedRectangleTask(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long currentTimeMillis) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.z_index = z_index;
            this.texture = texture;
            this.translation = translation;
            this.velocity = velocity;
            this.timestamp = currentTimeMillis;
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
            r.drawnElements.put(id, new TexturedRectangle(this.left, this.right, this.top, this.bottom, this.z_index, this.texture.get(), this.translation, this.velocity, this.timestamp));

            try {
                this.callbackQueue.put(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Async<Integer> createTexturedRectangle(float left, float right, float top, float bottom, float z_index, Async<Texture> texture, Vector3f translation, Vector3f velocity, long currentTimeMillis) {
        CreateTexturedRectangleTask tsk = new CreateTexturedRectangleTask(left, right, top, bottom, z_index, texture, translation, velocity, currentTimeMillis);
        try {
            this.taskQueue.put(tsk);
            return new Async<>(tsk.callbackQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class UpdatePositionTask implements Task {
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

    static class DeleteDrawableTask implements Task {
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
}