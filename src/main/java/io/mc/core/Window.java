package io.mc.core;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

import io.mc.input.*;

public class Window {
    private static final Logger logger = Logger.getLogger(Window.class.getName());

    int width;
    int height;

    String title;

    static long windowPtr;

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
        int glfwCursorMode =
			cursorMode == CursorMode.LOCKED ? GLFW_CURSOR_DISABLED :
			cursorMode == CursorMode.NORMAL ? GLFW_CURSOR_NORMAL :
			cursorMode == CursorMode.HIDDEN ? GLFW_CURSOR_HIDDEN :
			GLFW_CURSOR_HIDDEN;

		glfwSetInputMode(windowPtr, GLFW.GLFW_CURSOR, glfwCursorMode);
    }

    void setTitle(String title) {

    }

    void setSize(int width, int height) {

    }

    // Commenting out due to being a constructor
    public static Window create(String title) {
        Window res = new Window();
        long monitor = GLFW.glfwGetPrimaryMonitor();

        if (monitor == MemoryUtil.NULL) {
            logger.severe("Failed to get primary monitor");
            return null;
        }

        final GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

        if (mode == null) {
            logger.severe("Failed to get video mode of primary monitor.");
            return null;
        }

        logger.info("Montior size: " + mode.WIDTH + " " + mode.HEIGHT);

        res.width = mode.WIDTH / 2;
        res.height = mode.HEIGHT / 2;
        res.title = title;

        res.windowPtr = GLFW.glfwCreateWindow(res.width, res.height, title, monitor, MemoryUtil.NULL);

        if (res.windowPtr == MemoryUtil.NULL) {
            GLFW.glfwTerminate();
            logger.severe("Failed to create Window");
            return res;
        }

        logger.info("GLFW window created");

        // GLFW.glfwSetWindowUserPointer(res.windowPtr, GLFW.monitor);
        GLFW.glfwMakeContextCurrent(res.windowPtr);

        // GLFW.glfwSetCursorPosCallback(res.windowPtr, Input.mouseCallback);
        // GLFW.glfwSetKeyCallback(Window.windowPtr, Input::keyCallback);
        // GLFW.glfwSetFramebufferSizeCallback(windowPtr,resizeCallback);
        // GLFW.glfwSetMouseButtonCallback((GLFWwindow*)res->windowPtr,
        // Input::mouseButtonCallback);
        // GLFW.glfwSetCharCallback((GLFWwindow*)res->windowPtr, Input::charCallback);
        // GLFW.glfwSetScrollCallback((GLFWwindow*)res->windowPtr,
        // Input::scrollCallback);

        Integer monitorX;
        Integer monitorY;
        // glfwGetMonitorPos(monitor, monitorX, monitorY);

        res.setVsync(true);
        return res;
    }

    public static void init() {
        GLFWErrorCallback.createPrint(System.err).set();

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
