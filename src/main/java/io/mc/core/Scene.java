package io.mc.core;

import java.util.logging.Logger;

public class Scene {
    private static final Logger logger = Logger.getLogger(Scene.class.getName());

    // Global variables
    public boolean serializeEvents = false;
    public boolean playFromEventFile = false;

    // Internal variables
    private static SceneType currentScene = SceneType.None;
    private static SceneType nextSceneType = SceneType.None;
    private static boolean changeSceneAtFrameEnd = false;

    public static void init(SceneType sceneType) {

    }

    public void update() {

    }

    public void changeScene(SceneType type) {

    }

    public void reloadShaders() {

    }

    public void queueMainEvent(GEventType type) {

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

    }

    public boolean isPlayingGame() {
        return currentScene == SceneType.SinglePlayerGame;
    }

    // Internal functions
    private static void changeSceneInternal() {

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
