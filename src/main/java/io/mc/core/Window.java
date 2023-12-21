package io.mc.core;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import io.mc.input.*;

public class Window {
    private static final Logger logger = Logger.getLogger(Window.class.getName());

    int width;
    int height;

    String title;
    long windowPtr;


    public Window(String title) {
        long monitor = GLFW.glfwGetPrimaryMonitor();

        if (monitor == MemoryUtil.NULL) {
            logger.severe("Failed to get primary monitor");
            
        }

        final GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

        if (mode == null) {
            logger.severe("Failed to get video mode of primary monitor.");
            
        }

        logger.info(String.format("Montior size: %d, %d", mode.WIDTH, mode.HEIGHT));

        this.width = mode.WIDTH / 2;
        this.height = mode.HEIGHT / 2;
        this.title = title;

        this.windowPtr = GLFW.glfwCreateWindow(this.width, this.height, title, monitor, MemoryUtil.NULL);

        if (this.windowPtr == MemoryUtil.NULL) {
            GLFW.glfwTerminate();
            logger.severe("Failed to create Window");
            
        }

        logger.info("GLFW window created");

        int monitorX;
        int monitorY;


        this.setVsync(true);
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

    }

    void setTitle(String title) {

    }

    void setSize(int width, int height) {

    }
    // Commenting out due to being a constructor
    // static Window create(String title) {
    //     Window res = new Window();
    //     long monitor = GLFW.glfwGetPrimaryMonitor();

    //     if (monitor == MemoryUtil.NULL) {
    //         logger.severe("Failed to get primary monitor");
    //         return null;
    //     }

    //     final GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

    //     if (mode == null) {
    //         logger.severe("Failed to get video mode of primary monitor.");
    //         return null;
    //     }

    //     logger.info(String.format("Montior size: %d, %d", mode.WIDTH, mode.HEIGHT));

    //     res.width = mode.WIDTH / 2;
    //     res.height = mode.HEIGHT / 2;
    //     res.title = title;

    //     res.windowPtr = GLFW.glfwCreateWindow(res.width, res.height, title, monitor, MemoryUtil.NULL);

    //     if (res.windowPtr == MemoryUtil.NULL) {
    //         GLFW.glfwTerminate();
    //         logger.severe("Failed to create Window");
    //         return res;
    //     }

    //     logger.info("GLFW window created");

    //     int monitorX;
    //     int monitorY;


    //     res.setVsync(true);
    //     return res;
    // }

    public void init() {
        GLFW.glfwInit();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
    }

    public static void free() {

    }

    public void pollInput() {
        Input.endFrame();
        GLFW.glfwPollEvents();
    }

    

    void destroy() {
        GLFW.glfwDestroyWindow(windowPtr);
    }

    boolean shouldClose() {
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

    
}

enum CursorMode {
    HIDDEN,
    LOCKED,
    NORMAL
}
