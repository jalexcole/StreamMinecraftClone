package io.mc.world;

import java.util.concurrent.atomic.AtomicReference;

import dev.dominion.ecs.api.Dominion;
import io.mc.renderer.Frustum;
import io.mc.renderer.Texture;

public class World {
    public static String savePath = "";
    static int seed = Integer.MAX_VALUE;
    static AtomicReference<Float> seedAsFloat = new AtomicReference(0.0f);
    static String chunkSavePath = "";
    static int worldTime = 0;
    static boolean doDaylightCycle = false;
    static float deltaTime = 0;
    static String localPlayerName = "(null)";

    public static final short CHUNK_RADIUS = 12;
    public static final short CHUNK_CAPACITY = (short) ((CHUNK_RADIUS * 2) * (CHUNK_RADIUS * 2) * 1.5f);
    public static final short ChunkWidth = 16;
    public static final short ChunkDepth = 16;
    public static final short ChunkHeight = 256;

    public static final
    short MaxVertsPerSubChunk = 2_500;

    public static void init(Dominion dominion, boolean isLanClient) {

    }

    public static void free(boolean shouldSerialize) {

    }

    public static void reloadShaders() {

    }

    void regenerateWorld() {

    }

    public static void update(Frustum camerFrustum, Texture worldTexture) {

    }

    public static String getWorldReplayDirPath(String savePath2) {
        return null;
    }

    public static void popSavePath() {
    }

    public static void free() {
    }

    public static float getDeltaTime() {
        return 0;
    }

    public static Object getLocalPlayer() {
        return null;
    }
}
