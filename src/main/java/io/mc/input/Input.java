package io.mc.input;

import java.util.logging.Logger;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Input {
    float mouseScreenX = 0;
    float mouseScreenY = 0;
    float mouseX = 0;
    float mouseY = 0;
    float lastMouseX = 0;
    float lastMouseY = 0;
    float deltaMouseX = 0;
    float deltaMouseY = 0;
    float mouseScrollX = 0;
    float mouseScrollY = 0;
    // boolean keyPressed[GLFW.GLFW_KEY_LAST] = {};
    // boolean keyBeginPressData[GLFW_KEY_LAST] = {};
    // boolean mousePressed[GLFW_MOUSE_BUTTON_LAST] = {};
    // boolean mouseBeginPressData[GLFW_MOUSE_BUTTON_LAST] = {};

    char lastCharPressedData = '\0';

    static boolean mFirstMouse = true;
    static Vector2f windowSize = new Vector2f();
    static Matrix4f inverseProjectionMatrix = new Matrix4f();
    private static final Logger logger = Logger.getLogger(Input.class.getName());
    public void setProjectionMatrix(Matrix4f inProjectionMatrix) {
        inverseProjectionMatrix = inProjectionMatrix.invert();

    }

    public static void setWindowSize(Vector2f inWindowSize) {
        windowSize = inWindowSize;
    }

    void mouseButtonCallback(long window, int button, int action, int mods) {

    }

    void processKeyEvent(int key, int action) {

    }

    public void endFrame() {
        deltaMouseX = 0;
        deltaMouseY = 0;
        lastCharPressedData = '\0';
        mouseScrollX = 0;
        mouseScrollY = 0;
    }

    char lastCharPressed() {
        return lastCharPressedData;
    }

    public static void setWindowSize(int newWidth, int newHeight) {
    }

}
