package io.mc.core;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Renderer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import dev.dominion.ecs.api.Dominion;
import io.mc.input.Input;
import io.mc.renderer.FrameBuffer;
import io.mc.renderer.Shader;
import io.mc.renderer.Sprites;

public class Application {

    private Window window;

    static float deltaTime = 0.16f;

    private static FrameBuffer frameBuffer;
    private static FrameBuffer mainFramebuffer;
    private static boolean dumpScreenshot = false;
    private static boolean screenshotMustBeSquare = false;
    private static String screenshotName = "";


    private static final Dominion dominion = Dominion.create();
    private static Shader screenshader;
    static final ExecutorService globalThreadPool = Executors.newFixedThreadPool(10);

    static void init() {
        window = window.creat();
    }

    public static void run() {
        double previousTime = GLFW.glfwGetTime();
        boolean inMainMenu = true;
	    final float targetFps = 0.016f;
		final float nextTarget = 0.032f;
		while (!window.shouldClose()) {

		    double currentTime = glfwGetTime();
			deltaTime = (float)(currentTime - previousTime);


			

            // Only update the game if the window is not minimized
            if (getWindow().width > 0 && getWindow().height > 0)
            {
                if (mainFramebuffer.width != window.width || mainFramebuffer.height != getWindow().height)
                {
                    mainFramebuffer.width = getWindow().width;
                    mainFramebuffer.height = getWindow().height;
                    mainFramebuffer.regenerate();
                    mainFramebuffer.bind();
                    glViewport(0, 0, getWindow().width, getWindow().height);
                }

                // TODO: You're trying to debug the black screen because of the glClear framebuffer thing
                mainFramebuffer.bind();
                final GLenum mainDrawBuffer[3] = { GL_COLOR_ATTACHMENT0, GL_NONE, GL_NONE };
                glDrawBuffers(3, mainDrawBuffer);
                final float zeroFillerVec[] = { 0.0f, 0.0f, 0.0f, 1.0f };
                GL12.glClearBufferfv(GL11.GL_COLOR, 0, zeroFillerVec);
                float one = 1;
                glClearBufferfv(GL_DEPTH, 0, &one);
                Scene::update();

                // Unbind all framebuffers and render the composited image
                glDisable(GL_DEPTH_TEST);
                glDepthMask(GL_TRUE);
                glDisable(GL_BLEND);

                glBindFramebuffer(GL_FRAMEBUFFER, 0);
                glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

                screenShader.bind();
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, mainFramebuffer.getColorAttachment(0).graphicsId);
                screenShader.uploadInt("uMainTexture", 0);

                glBindVertexArray(Vertices::fullScreenSpaceRectangleVao);
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
                if (screenshotMustBeSquare)
                {
                    if (outputWidth > outputHeight)
                    {
                        outputWidth = outputHeight;
                        startX = (outputWidth - outputHeight) / 2;
                    }
                    else
                    {
                        outputHeight = outputWidth;
                        startY = (outputHeight - outputWidth) / 2;
                    }
                }

                glReadPixels(startX, startY, outputWidth, outputHeight, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
                stbi_flip_vertically_on_write(true);
                stbi_write_png(filepath.c_str(), outputWidth, outputHeight, 4, (void*)pixels, sizeof(uint8) * outputWidth * 4);
                g_logger_info("Screenshot saved to: %s", filepath.c_str());
                g_memory_free(pixels);
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
        globalThreadPool.free();
        globalThreadPool = null;

        Vertices.free();
        GuiElements.free();
        getRegistry().free();
        Window window = Window.getWindow();
        window.destroy();
        Renderer.free();
        Window.free();

        // Free the pointers now that everything should be cleaned up
        freeWindow();
        freeRegistry();
    }

    void takeScreenshot(String filename, boolean mustBeSquare) {

    }

    Window getWindow() {
        return window;
    }

    FrameBuffer getMainFramebuffer() {
        return frameBuffer;
    }

    Executor getGlobalThreadPool() {
        return globalThreadPool;
    }

}
