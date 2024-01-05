package io.mc.core;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

import dev.dominion.ecs.api.Dominion;
import io.mc.gui.Gui;
import io.mc.input.Input;
import io.mc.physics.Physics;
import io.mc.renderer.ByteFormat;
import io.mc.renderer.FilterMode;
import io.mc.renderer.FrameBuffer;
import io.mc.renderer.Renderer;
import io.mc.renderer.Shader;
import io.mc.renderer.Sprites;
import io.mc.renderer.Texture;
import io.mc.renderer.TextureType;
import io.mc.renderer.WrapMode;
import io.mc.renderer.FrameBuffer.FramebufferBuilder;

public class Application {

    private static Window window;

    public static float deltaTime = 0.16f;

    // private static FrameBuffer frameBuffer = null;
    private static FrameBuffer mainFramebuffer;
    private static boolean dumpScreenshot = false;
    private static boolean screenshotMustBeSquare = false;
    private static String screenshotName = "";

    private static Dominion registry = Dominion.create();
    private static Shader screenShader;

    private static ByteBuffer pixels;
    static final ExecutorService globalThreadPool = Executors.newFixedThreadPool(10);
    private static final String title = "MineCraft";

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public static void init() {
        Window.init();
        Application.window = Window.create("Minecraft");

        if (Window.windowPtr == NULL) {
            logger.severe("Error: Could not create window");
        }
        AppData.init();
        registry = Dominion.create();
        Renderer.init(registry);
        Fonts.init();
        Physics.init();
        Scene.init(SceneType.MainMenu, registry);
        KeyBindings.init();
        Gui.init();
        GuiElements.init();

        // Allocate some GPU memory for basic geometry VAOs
        Vertices.init();

        final Texture opaqueTextureSpec = new Texture();
        opaqueTextureSpec.type = TextureType.TWO_D;
        opaqueTextureSpec.width = window.width;
        opaqueTextureSpec.height = window.height;
        opaqueTextureSpec.magFilter = FilterMode.Linear;
        opaqueTextureSpec.minFilter = FilterMode.Linear;
        opaqueTextureSpec.wrapS = WrapMode.None;
        opaqueTextureSpec.wrapT = WrapMode.None;
        opaqueTextureSpec.format = ByteFormat.RGBA_16F;
        opaqueTextureSpec.path = "";

        final Texture accumulationTextureSpec = new Texture();
        accumulationTextureSpec.type = TextureType.TWO_D;
        accumulationTextureSpec.width = window.width;
        accumulationTextureSpec.height = window.height;
        accumulationTextureSpec.magFilter = FilterMode.Linear;
        accumulationTextureSpec.minFilter = FilterMode.Linear;
        accumulationTextureSpec.wrapS = WrapMode.None;
        accumulationTextureSpec.wrapT = WrapMode.None;
        accumulationTextureSpec.format = ByteFormat.RGBA_16F;
        accumulationTextureSpec.path = "";

        final Texture revealTextureSpec = new Texture();
        revealTextureSpec.type = TextureType.TWO_D;
        revealTextureSpec.width = window.width;
        revealTextureSpec.height = window.height;
        revealTextureSpec.magFilter = FilterMode.Linear;
        revealTextureSpec.minFilter = FilterMode.Linear;
        revealTextureSpec.wrapS = WrapMode.None;
        revealTextureSpec.wrapT = WrapMode.None;
        revealTextureSpec.format = ByteFormat.R8_F;
        revealTextureSpec.path = "";

        mainFramebuffer = new FramebufferBuilder(window.width, window.height).addColorAttachment(opaqueTextureSpec)
                .addColorAttachment(accumulationTextureSpec).addColorAttachment(revealTextureSpec)
                .includeDepthStencilBuffer()
                .generate();
        // screenShader.compile("assets/shaders/MainFramebuffer.glsl");
    }

    public static void run() {
        double previousTime = GLFW.glfwGetTime();
        boolean inMainMenu = true;
        final float targetFps = 0.016f;
        final float nextTarget = 0.032f;
        while (!window.shouldClose()) {

            double currentTime = glfwGetTime();
            deltaTime = (float) (currentTime - previousTime);

            // Only update the game if the window is not minimized
            if (getWindow().width > 0 && getWindow().height > 0) {
                if (mainFramebuffer.width != window.width || mainFramebuffer.height != getWindow().height) {
                    mainFramebuffer.width = getWindow().width;
                    mainFramebuffer.height = getWindow().height;
                    mainFramebuffer.regenerate();
                    mainFramebuffer.bind();
                    glViewport(0, 0, getWindow().width, getWindow().height);
                }

                // TODO: You're trying to debug the black screen because of the glClear
                // framebuffer thing
                mainFramebuffer.bind();
                final int[] mainDrawBuffer = { GL_COLOR_ATTACHMENT0, GL_NONE, GL_NONE };
                glDrawBuffers(mainDrawBuffer);

                final float[] zeroFillerVec = { 0.0f, 0.0f, 0.0f, 1.0f };
                glClearBufferfv(GL11.GL_COLOR, 0, zeroFillerVec);
                float one = 1;
                glClearBufferfv(GL_DEPTH, 0, FloatBuffer.allocate(3));
                Scene.update();

                // Unbind all framebuffers and render the composited image
                glDisable(GL_DEPTH_TEST);
                glDepthMask(true);
                glDisable(GL_BLEND);

                glBindFramebuffer(GL_FRAMEBUFFER, 0);
                glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

                screenShader.bind();
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, 0);
                screenShader.uploadInt("uMainTexture", 0);

                glBindVertexArray(Vertices.fullScreenSpaceRectangleVao);
                glDrawArrays(GL_TRIANGLES, 0, 6);

                glEnable(GL_DEPTH_TEST);
            }

            window.swapBuffers();
            window.pollInput();

            if (dumpScreenshot) {

                int outputWidth = mainFramebuffer.width;
                int outputHeight = mainFramebuffer.height;
                int startX = 0;
                int startY = 0;
                if (screenshotMustBeSquare) {
                    if (outputWidth > outputHeight) {
                        outputWidth = outputHeight;
                        startX = (outputWidth - outputHeight) / 2;
                    } else {
                        outputHeight = outputWidth;
                        startY = (outputHeight - outputWidth) / 2;
                    }
                }

                glReadPixels(startX, startY, outputWidth, outputHeight, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

                dumpScreenshot = false;
            }

            previousTime = currentTime;
        }
    }

    public static void free() {
        // Free our assets
        screenShader.destroy();
        Sprites.freeAllSpritesheets();
        Fonts.unloadAllFonts();
        mainFramebuffer.destroy();

        // Free all resources
        // Important: Scene gets freed first so that it queues all saving tasks to the
        // global thread pool.
        // Then we can free the global thread pool which will finish those tasks
        Scene.free();
        globalThreadPool.shutdown();

        Vertices.free();
        GuiElements.free();
        getRegistry().close();
        Window window = Window.getWindow();
        window.destroy();
        Renderer.free();
        Window.free();

        // Free the pointers now that everything should be cleaned up
        freeWindow();
        freeRegistry();
    }

    private static void freeRegistry() {
    }

    private static void freeWindow() {
        window = null;
    }

    private static Dominion getRegistry() {
        return registry;
    }

    void takeScreenshot(String filename, boolean mustBeSquare) {

    }

    public static Window getWindow() {
        return window;
    }

    public static FrameBuffer getMainFramebuffer() {
        return mainFramebuffer;
    }

    public static Executor getGlobalThreadPool() {
        return globalThreadPool;
    }

}
