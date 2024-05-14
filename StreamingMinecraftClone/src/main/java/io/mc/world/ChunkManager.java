package io.mc.world;

import org.joml.Vector2i;
import org.joml.Vector3f;

public class ChunkManager {

    public static Block getBlock(Vector3f position) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlock'");
    }

}

class DrawCommand {

}

class DrawCommandUtil {

}

class CommandBufferContainer {

    private int maxNumCommands;
    private int numCommands;
    boolean isTransparent;
    DrawCommand commandBuffer;
    int chunkPosBuffer;
    int biomeBuffer;

    CommandBufferContainer(int maxNumCommands, boolean isTransparent) {
        this.maxNumCommands = maxNumCommands;
        this.isTransparent = isTransparent;
        commandBuffer = 0;
        chunkPosBuffer = 0;
        biomeBuffer = 0;
        numCommands = 0;
    }

}