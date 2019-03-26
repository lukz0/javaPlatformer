import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer implements Runnable {
    private long window;

    private final Lock background_color_lock = new ReentrantLock(true);
    private float background_color_red = 1;
    private float background_color_green = 0;
    private float background_color_blue = 1;
    private boolean background_color_modified = false;

    private Thread t;

    private GLFWKeyCallback keyHandler;

    Renderer(GLFWKeyCallback keyHandler) {
        this.keyHandler = keyHandler;
    }

    public void run() {
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
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        //glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        // Create the window
        window = glfwCreateWindow(800, 800, "Project Mario!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, this.keyHandler);

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
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

        // Set the clear color
        glClearColor(background_color_red, background_color_green, background_color_blue, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            if (this.background_color_modified) {
                this.background_color_lock.lock();
                glClearColor(this.background_color_red,
                        this.background_color_green,
                        this.background_color_blue,
                        0.0f);
                this.background_color_modified = false;
                this.background_color_lock.unlock();
            }
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    void setBackgroundColor(float r, float g, float b) {
        this.background_color_lock.lock();
        this.background_color_modified = true;
        this.background_color_red = r;
        this.background_color_green = g;
        this.background_color_blue = b;
        this.background_color_lock.unlock();
    }

    static class Vector3f {
        float[] values;
        Vector3f(float x1, float x2, float x3) {
            this.values = new float[] {x1, x2, x3};
        }
    }

    static class Matrix4f {
        float[] values;
        // v(row)(column)
        Matrix4f(float v11, float v21, float v31, float v41,
                 float v12, float v22, float v32, float v42,
                 float v13, float v23, float v33, float v43,
                 float v14, float v24, float v34, float v44) {
            this.values = new float[] {
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
                    -(left + right)/2.0f, -(top + bottom)/2.0f, -(near + far)/2.0f, 1
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
            // TODO
            return new Matrix4f(
                    // first row this, first column matrix
                    this.values[0+0*4]*matrix.values[0+0*4] + this.values[0+1*4]*matrix.values[1+0*4] + this.values[0+2*4]*matrix.values[2+0*4] + this.values[0+3*4]*matrix.values[3+0*4],
                    // second row this, first column matrix
                    this.values[1+0*4]*matrix.values[0+0*4] + this.values[1+1*4]*matrix.values[1+0*4] + this.values[1+2*4]*matrix.values[2+0*4] + this.values[1+3*4]*matrix.values[3+0*4],
                    // third row this, first column matrix
                    this.values[2+0*4]*matrix.values[0+0*4] + this.values[2+1*4]*matrix.values[1+0*4] + this.values[2+2*4]*matrix.values[2+0*4] + this.values[2+3*4]*matrix.values[3+0*4],
                    // fourth row this, first column matrix
                    this.values[3+0*4]*matrix.values[0+0*4] + this.values[3+1*4]*matrix.values[1+0*4] + this.values[3+2*4]*matrix.values[2+0*4] + this.values[3+3*4]*matrix.values[3+0*4],
                    //
                    // first row this, second column matrix
                    this.values[0+0*4]*matrix.values[0+1*4] + this.values[0+1*4]*matrix.values[1+1*4] + this.values[0+2*4]*matrix.values[2+1*4] + this.values[0+3*4]*matrix.values[3+1*4],
                    // second row this, second column matrix
                    this.values[1+0*4]*matrix.values[0+1*4] + this.values[1+1*4]*matrix.values[1+1*4] + this.values[1+2*4]*matrix.values[2+1*4] + this.values[1+3*4]*matrix.values[3+1*4],
                    // third row this, second column matrix
                    this.values[2+0*4]*matrix.values[0+1*4] + this.values[2+1*4]*matrix.values[1+1*4] + this.values[2+2*4]*matrix.values[2+1*4] + this.values[2+3*4]*matrix.values[3+1*4],
                    // fourth row this, second column matrix
                    this.values[3+0*4]*matrix.values[0+1*4] + this.values[3+1*4]*matrix.values[1+1*4] + this.values[3+2*4]*matrix.values[2+1*4] + this.values[3+3*4]*matrix.values[3+1*4],
                    //
                    // first row this, third column matrix
                    this.values[0+0*4]*matrix.values[0+2*4] + this.values[0+1*4]*matrix.values[1+2*4] + this.values[0+2*4]*matrix.values[2+2*4] + this.values[0+3*4]*matrix.values[3+2*4],
                    // second row this, third column matrix
                    this.values[1+0*4]*matrix.values[0+2*4] + this.values[1+1*4]*matrix.values[1+2*4] + this.values[1+2*4]*matrix.values[2+2*4] + this.values[1+3*4]*matrix.values[3+2*4],
                    // third row this, third column matrix
                    this.values[2+0*4]*matrix.values[0+2*4] + this.values[2+1*4]*matrix.values[1+2*4] + this.values[2+2*4]*matrix.values[2+2*4] + this.values[2+3*4]*matrix.values[3+2*4],
                    // fourth row this, third column matrix
                    this.values[3+0*4]*matrix.values[0+2*4] + this.values[3+1*4]*matrix.values[1+2*4] + this.values[3+2*4]*matrix.values[2+2*4] + this.values[3+3*4]*matrix.values[3+2*4],
                    //
                    // first row this, fourth column matrix
                    this.values[0+0*4]*matrix.values[0+3*4] + this.values[0+1*4]*matrix.values[1+3*4] + this.values[0+2*4]*matrix.values[2+3*4] + this.values[0+3*4]*matrix.values[3+3*4],
                    // second row this, fourth column matrix
                    this.values[1+0*4]*matrix.values[0+3*4] + this.values[1+1*4]*matrix.values[1+3*4] + this.values[1+2*4]*matrix.values[2+3*4] + this.values[1+3*4]*matrix.values[3+3*4],
                    // third row this, fourth column matrix
                    this.values[2+0*4]*matrix.values[0+3*4] + this.values[2+1*4]*matrix.values[1+3*4] + this.values[2+2*4]*matrix.values[2+3*4] + this.values[2+3*4]*matrix.values[3+3*4],
                    // fourth row this, fourth column matrix
                    this.values[3+0*4]*matrix.values[0+3*4] + this.values[3+1*4]*matrix.values[1+3*4] + this.values[3+2*4]*matrix.values[2+3*4] + this.values[3+3*4]*matrix.values[3+3*4]
            );
        }
    }
}