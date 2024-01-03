package io.mc.input;

import java.util.logging.Logger;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import io.mc.core.Scene;

public class Input {
    public static float mouseScreenX = 0;
    public static float mouseScreenY = 0;
    float mouseX = 0;
    float mouseY = 0;
    float lastMouseX = 0;
    float lastMouseY = 0;
    static float deltaMouseX = 0;
    static float deltaMouseY = 0;
    static float mouseScrollX = 0;
    static float mouseScrollY = 0;
    // boolean keyPressed[GLFW.GLFW_KEY_LAST] = {};
    // boolean keyBeginPressData[GLFW_KEY_LAST] = {};
    // boolean mousePressed[GLFW_MOUSE_BUTTON_LAST] = {};
    // boolean mouseBeginPressData[GLFW_MOUSE_BUTTON_LAST] = {};

    static char lastCharPressedData = '\0';

    static boolean mFirstMouse = true;
    static Vector2f windowSize = new Vector2f();
    static Matrix4f inverseProjectionMatrix = new Matrix4f();

    private static boolean[] keyPressed = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] keyBeginPressData = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] mousePressed = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] mouseBeginPressData = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private static final Logger logger = Logger.getLogger(Input.class.getName());
    public void setProjectionMatrix(@NonNull Matrix4f inProjectionMatrix) {
        inverseProjectionMatrix = inProjectionMatrix.invert();

    }

    public static void setWindowSize(@NonNull Vector2f inWindowSize) {
        windowSize = inWindowSize;
    }

    public static void mouseButtonCallback(long window, int key, int scancode, int mods) {
        // Scene.queueMainEventMouse((float) xpos, (float) ypos);
    }

    void processKeyEvent(int key, int action) {

    }

    public static void endFrame() {
        deltaMouseX = 0;
        deltaMouseY = 0;
        lastCharPressedData = '\0';
        mouseScrollX = 0;
        mouseScrollY = 0;
    }

    public static char lastCharPressed() {
        return lastCharPressedData;
    }

    public static void setWindowSize(int newWidth, int newHeight) {
    }

    public static boolean mouseBeginPress(int mouseButton) {
        return mouseBeginPressData[mouseButton];
    }

    public static boolean isMousePressed(int glfwMouseButtonLeft) {
        return false;
    }

    public static boolean isKeyPressed(int glfwKeyBackspace) {
        return false;
    }



}
