package io.mc.core;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

import io.mc.input.*;

public class Window {
    private static final Logger logger = Logger.getLogger(Window.class.getName());

    int width = 1280;
    int height = 720;

    String title;

    static long windowPtr = NULL;

    private Window() {

    }

    private static void resizeCallback(long windotPtr, int newWidth, int newHeight) {
        long userWindow = GLFW.glfwGetWindowUserPointer(windotPtr);
        GLFW.glfwSetWindowSize(windotPtr, newWidth, newHeight);
        Input.setWindowSize(newWidth, newHeight);

    }

    public void makeContextCurrent() {
        GLFW.glfwMakeContextCurrent(windowPtr);
    }

    public void close() {
        GLFW.glfwSetWindowShouldClose(windowPtr, true);
    }

    void setCursorMode(CursorMode cursorMode) {
        int glfwCursorMode = cursorMode == CursorMode.LOCKED ? GLFW_CURSOR_DISABLED
                : cursorMode == CursorMode.NORMAL ? GLFW_CURSOR_NORMAL
                        : cursorMode == CursorMode.HIDDEN ? GLFW_CURSOR_HIDDEN : GLFW_CURSOR_HIDDEN;

        glfwSetInputMode(windowPtr, GLFW.GLFW_CURSOR, glfwCursorMode);
    }

    void setTitle(String title) {

    }

    void setSize(int width, int height) {

    }

    // Commenting out due to being a constructor
    public static Window create(String title) {
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

    public static void init() {

        var glfwInit = GLFW.glfwInit();

        if (!glfwInit)
            logger.severe("Unable to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
    }

    public static void free() {
        GLFW.glfwTerminate();
    }

    public void pollInput() {
        Input.endFrame();
        GLFW.glfwPollEvents();
    }

    static void destroy() {
        GLFW.glfwDestroyWindow(windowPtr);
    }

    static boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(windowPtr);
    }

    void swapBuffers() {
        GLFW.glfwSwapBuffers(windowPtr);
    }

    float getAspectRatio() {
        return (float) width / (float) height;
    }

    void setVsync(boolean on) {
        if (on) {
            GLFW.glfwSwapInterval(1);
        } else {
            GLFW.glfwSwapInterval(0);
        }
    }

    public static Window getWindow() {
        return null;
    }

}
