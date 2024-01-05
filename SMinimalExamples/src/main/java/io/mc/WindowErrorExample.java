package io.mc;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class WindowErrorExample {

    public static void main(String[] args) {
        Application app = new Application();
        app.init();
    }

}

class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private static Window window = null;

    public Application() {

    }

    public void init() {
        this.window = Window.create("Minecraft");
        window.init();
        if (Window.windowPtr == NULL) {
            logger.severe("Error: Could not create window");
        }
    }

}

class Window {
    private static final Logger logger = Logger.getLogger(Window.class.getName());
    static long windowPtr = NULL;
    private static int width = 720;
    private static int height = 1280;
    private @NonNull String title;

    static Window create(@NonNull String title) {
        if (!GLFW.glfwInit())
            logger.severe("Unable to initialize GLFW");

        GLFWErrorCallback.createPrint(System.err).set();

        final Window res = new Window();

        GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable

        res.windowPtr = GLFW.glfwCreateWindow(res.width, res.height, title, NULL, MemoryUtil.NULL);

        if (windowPtr == NULL) {
            logger.severe("Failed to create window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(windowPtr, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            // Center the window
            GLFW.glfwSetWindowPos(
                    windowPtr,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getCause());
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowPtr);
        // Enable v-sync
        GLFW.glfwSwapInterval(1);

        // Make the window visible
        GLFW.glfwShowWindow(windowPtr);

        res.title = title;

        return res;
    }

    public void init() {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
    }

    

}
