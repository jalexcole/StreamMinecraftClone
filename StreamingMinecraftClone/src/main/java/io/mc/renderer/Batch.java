package io.mc.renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL;

public class Batch<T> {

    public static final Logger logger = Logger.getLogger(Batch.class.getName());

    public static final int maxBatchSize = 501;
    public static final int numTextureGraphicsIds = 16;

    int vao;
    int vbo;
    public int numVertices;
    public int zIndex;
    public List<Integer> textureGraphicsIds = new ArrayList<>();
    int dataSize;
    public T data;

    public void init(List<VertexAttribute> vertexAttributes) {

    }

    public void addVertex(final T vertex) {
        if (numVertices >= maxBatchSize) {
            logger.warning("Batch ran out of room. I have " + numVertices + " / " + maxBatchSize + "vertices.");
            return;
        } else if (numVertices < 0) {
            logger.severe("Invalid vertex number");
            return;
        }

    }

    public void flush() {
    }

    public static int[] textureIndices() {
        return null;
    }

    public void free() {
        if (data != null) {
            data = null;
            dataSize = 0;
        }
    }

    public boolean hasTexture(int textureGraphicsId) {
        for (int i = 0; i < textureGraphicsIds.size(); i++) {
            if (textureGraphicsIds.get(i) == textureGraphicsId) {
                return true;
            }
        }
        return false;
    }

    public static int toGl(AttributeType type) {
        switch (type) {
            case Float:
                return GL_FLOAT;
            case Int:
                return GL_INT;
            case Uint:
                return GL_UNSIGNED_INT;
            default:
                return GL_NONE;

        }
    }

    public static final int[] textureIndeces() {
        int[] res = new int[numTextureGraphicsIds];

        for (int i = 0; i < numTextureGraphicsIds; i++) {
            res[i] = i;
        }

        return res;
    }

    public boolean hasRoom() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasRoom'");
    }

    public int getTextureSlot(int graphicsId, boolean isFont) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTextureSlot'");
    }

    public boolean hasTextureRoom(boolean isFont) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasTextureRoom'");
    }
}
