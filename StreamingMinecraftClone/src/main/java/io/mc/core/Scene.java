package io.mc.core;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.jspecify.annotations.NonNull;

import dev.dominion.ecs.api.Dominion;
import io.mc.gui.Gui;
import io.mc.gui.MainMenu;
import io.mc.renderer.Frustum;
import io.mc.renderer.Renderer;
import io.mc.renderer.Texture;
import io.mc.renderer.Texture.TextureBuilder;
import io.mc.utils.TexturePacker;
import io.mc.world.BlockMap;
import io.mc.world.World;

public class Scene {
    private static final Logger logger = Logger.getLogger(Scene.class.getName());

    // Global variables
    public static boolean serializeEvents = false;
    public static boolean playFromEventFile = false;

    // Internal variables
    private static SceneType currentScene = SceneType.None;
    private static SceneType nextSceneType = SceneType.None;
    private static boolean changeSceneAtFrameEnd = false;
    private static Dominion registry = null;
    private static Texture worldTexture = null;
    private static Texture itemTexture = null;

    private static Texture blockItemTexture = null;
    private static Queue<GEvent> events = new LinkedList<>();

    private static File serializedEventFile = null;
    
    // TODO: Where does this go??
    public static Camera camera = null;
    public static Frustum frustum = null;

    private static boolean changedSceneAtFrameEnd;

    private static Frustum cameraFrustum;
    
    
    private Scene() {

    }

    public static void init(@NonNull final SceneType sceneType, @NonNull final Dominion registry) {
        // var file = Files.createDirectory("assets/generated", null);

        final String packedTecturesFilePath = "assets/generated/packedTextures.png";
        final String packedItemTextureFilePath = "assets/generated/pacledItemTexture.png";

        TexturePacker.packTextures("assets/image/item", "assets/generated/itemTextureFormat.yaml");
        TexturePacker.packTextures("assets/images/item", "assets/generated/itemTextureFormat.yaml", packedItemTexturesFilepath, "Items");
		BlockMap.loadBlocks("assets/generated/textureFormat.yaml", "assets/generated/itemTextureFormat.yaml", "assets/custom/blockFormats.yaml");
		BlockMap.uploadTextureCoordinateMapToGpu();
        Scene.registry = registry;

        
    }

    public static void update() {
        Gui.beginFrame();

        if (serializedEventFile == null && (serializeEvents || playFromEventFile)) {

            String eventFilepath = World.getWorldReplayDirPath(World.savePath) + "/replay.bin";

            if (playFromEventFile) {
                eventFilepath = World.savePath + "/replay.bin";
            }

            if (serializeEvents) {
                serializedEventFile = new File(eventFilepath);

            }
        }
        
        queueMainEvent(GEventType.SetDeltaTime, Application.deltaTime, false);
        processEvent(null, 0, 0);

        switch (currentScene) {
            case LocalLanGame:
                break;
            case MainMenu:
                MainMenu.update();
                break;
            case MultiplayerGame:
                break;
            case None:
                break;
            case Replay:
                
                World.update(cameraFrustum, worldTexture);
                break;
            case SinglePlayerGame:
                break;
            default:
                logger.info("Cannot update unkown scene type");
                break;

        }

        Renderer.render();

        queueMainEvent(GEventType.FrameTick, 0, false);

        
        if (changedSceneAtFrameEnd) {
            changedSceneAtFrameEnd = false;
            changeSceneInternal();
        }

    }
    /**
     * Don't change the scene immediately, instead wait til the end of the frame
		so we don't disrupt any important simulations
     * @param type
     */
    public void changeScene(SceneType type) {
        Scene.changedSceneAtFrameEnd = true;
        nextSceneType = type;
    }

    public void reloadShaders() {
        Renderer.reloadShaders();
        switch (currentScene) {
            case LocalLanGame:
                break;
            case MainMenu:
                MainMenu.reloadShaders();
                break;
            case MultiplayerGame:
                break;
            case None:
                break;
            case Replay:
                World.reloadShaders();
                break;
            case SinglePlayerGame:
                break;
            default:
                break;

        }
    }

    public static void queueMainEvent(GEventType type, long eventData, float deltaTime, boolean freeData) {
        if (!playFromEventFile) {
           

        }
    }

    public void queueMainEventKey(int key, int action) {

    }

    public void queueMainEventMouse(float xpos, float ypos) {

    }

    public void queueMainEventChar(int codepoint) {

    }

    public void queueMainEventMouseButton(int button, int action) {

    }

    public void queueMainEventMouseScroll(float xoffset, float yoffset) {

    }

    public void queueMainEventMoustInitial(float xpos, float ypos, float lastMouseX, float lastMouseY) {

    }

    public static void free(boolean freeGlobalResources) {
        if (freeGlobalResources) {
            worldTexture.destroy();
            itemTexture.destroy();
            blockItemTexture.destroy();
        }

        if (serializeEvents) {
            serializedEventFile = null;
            serializeEvents = false;
        }

        switch (currentScene) {
            case LocalLanGame:
                break;
            case MainMenu:
                MainMenu.free();
                break;
            case MultiplayerGame:
                World.free();
                break;
            case None:
                break;
            case Replay:
                World.free(false);
                World.popSavePath();
                break;
            case SinglePlayerGame:
                break;
            default:
                break;
        }

        currentScene = SceneType.None;
    }

    public boolean isPlayingGame() {
        return currentScene == SceneType.SinglePlayerGame;
    }

    Camera getCamera() {
        return camera;
    }

    // Internal functions
    private static void changeSceneInternal() {
        free(false);

        resetRegistry();

        switch (nextSceneType) {
            case LocalLanGame:
                World.init(registry, true);
                break;
            case MainMenu:
                break;
            case MultiplayerGame:
                World.init(registry, false);
                break;
            case None:
                break;
            case Replay:
                break;
            case SinglePlayerGame:
                World.init(registry, false);
                break;
            default:
                break;

        }

        currentScene = nextSceneType;
        nextSceneType = SceneType.None;
    }

    private static void resetRegistry() {
    }

    private static void addCameraToRegistry() {

    }

    private static void processEvents() {

    }

    private static void serializeEvent(GEvent event) {

    }

    private static void processEvent(GEventType type, long data, int sizeOfData) {

    }

    private static int getEventSize(GEventType type) {
        switch (type) {
            case SetDeltaTime:
                return 4;
            case PlayerKeyInput:
                return 4 * 2;
            case PlayerMouseInput:
                return 4 * 2;
            case MouseInitial:
                return 4 * 4;
            case SetPlayerPos:
                return 12;
            case SetPlayerViewAxis:
                return 8;
            case SetPlayerOrientation:
                return 12;
            case SetPlayerForward:
                return 12;
            case PlayerCharInput:
                return 4;
            case PlayerMouseButtonInput:
                return 4 * 2;
            case PlayerMouseScrollInput:
                return 4 * 2;
            // Zero sized events
            case FrameTick:
                return 0;
            default:
                logger.severe("Tried to get size of unknown event '%s' in World::getEventSize().");
                return -1;
        }
    }

    public static void free() {
    }
}

class GEvent {
    GEventType type;
    int size;
    long data;
    boolean freeData;
}

class Camera {

}

enum GEventType {
    None,
    SetDeltaTime,
    PlayerKeyInput,
    PlayerMouseInput,
    PlayerCharInput,
    PlayerMouseButtonInput,
    PlayerMouseScrollInput,
    MouseInitial,
    SetPlayerPos,
    SetPlayerViewAxis,
    SetPlayerOrientation,
    SetPlayerForward,
    FrameTick
}

enum SceneType {
    None,
    SinglePlayerGame,
    LocalLanGame,
    MultiplayerGame,
    MainMenu,
    Replay
}
