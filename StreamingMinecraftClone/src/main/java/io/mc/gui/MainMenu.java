package io.mc.gui;

import java.util.Map;
import java.util.logging.Logger;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import io.mc.renderer.Shader;
import io.mc.renderer.Sprite;
import io.mc.renderer.Sprites;
import io.mc.world.World;

public class MainMenu {

    static Sprite title;
    static Vector2f titleSize;
    static boolean isCreatingWorld;
    static boolean isJoiningLanServer;

    static Cubemap menuSkybox;
    static Matrix4f projectionMatrix;
    static Matrix4f viewMatrix;
    static Vector3f viewAxis;
    static float viewRotation;
    static Shader cubemapShader;

    private static final Logger logger = Logger.getLogger(MainMenu.class.getName());

    void init() {
        resetState();

        final Map<String, Sprite> menuSprites = Sprites.getSpritesheet("assets/images/hudSpritesheet.yaml");

        titleSize = new Vector2f(2.0f, 0.5f);
        title = menuSprites.get("title");

        menuSkybox = Cubemap.generateCubemap(
                "assets/images/menuSkybox/Top.png",
                "assets/images/menuSkybox/Bottom.png",
                "assets/images/menuSkybox/Left.png",
                "assets/images/menuSkybox/Right.png",
                "assets/images/menuSkybox/Front.png",
                "assets/images/menuSkybox/Back.png");
        reloadShaders();
        viewAxis = new Vector3f(0.1f, 1.0f, -0.1f).normalize();
        viewRotation = 0.0f;

        CreateWorldMenu.init();
        LanServerMenu.init();
        logger.info("Initialized main menu scene.");
    }

    private void resetState() {
        World.setSavePath("");
			isCreatingWorld = false;
			isJoiningLanServer = false;

			LanServerMenu.resetState();
    }

    public static void free() {
        menuSkybox.destroy();
        cubemapShader.destroy();

        CreateWorldMenu.free();
        LanServerMenu.free();
    }

    public static void reloadShaders() {
        cubemapShader.destroy();
        cubemapShader.compile("assets/shaders/Cubemap.glsl");
    }

    public static void update() {

    }

}
