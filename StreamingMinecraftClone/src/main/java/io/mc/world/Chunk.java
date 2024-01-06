package io.mc.world;

import java.io.Serializable;

import org.joml.Vector2i;

public class Chunk implements Serializable {
    
    Block data;
    Vector2i chunkCoords;
    ChunkState state;
    boolean needsToGenerateDecorations;
    boolean needsToCalculateLighting;

    Chunk topNeighbor;
    Chunk bottomNeightbor;
    Chunk leftNeighbor;
    Chunk rightNeighbor;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((chunkCoords == null) ? 0 : chunkCoords.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + (needsToGenerateDecorations ? 1231 : 1237);
        result = prime * result + (needsToCalculateLighting ? 1231 : 1237);
        result = prime * result + ((topNeighbor == null) ? 0 : topNeighbor.hashCode());
        result = prime * result + ((bottomNeightbor == null) ? 0 : bottomNeightbor.hashCode());
        result = prime * result + ((leftNeighbor == null) ? 0 : leftNeighbor.hashCode());
        result = prime * result + ((rightNeighbor == null) ? 0 : rightNeighbor.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Chunk))
            return false;
        Chunk other = (Chunk) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (chunkCoords == null) {
            if (other.chunkCoords != null)
                return false;
        } else if (!chunkCoords.equals(other.chunkCoords))
            return false;
        if (state != other.state)
            return false;
        if (needsToGenerateDecorations != other.needsToGenerateDecorations)
            return false;
        if (needsToCalculateLighting != other.needsToCalculateLighting)
            return false;
        if (topNeighbor == null) {
            if (other.topNeighbor != null)
                return false;
        } else if (!topNeighbor.equals(other.topNeighbor))
            return false;
        if (bottomNeightbor == null) {
            if (other.bottomNeightbor != null)
                return false;
        } else if (!bottomNeightbor.equals(other.bottomNeightbor))
            return false;
        if (leftNeighbor == null) {
            if (other.leftNeighbor != null)
                return false;
        } else if (!leftNeighbor.equals(other.leftNeighbor))
            return false;
        if (rightNeighbor == null) {
            if (other.rightNeighbor != null)
                return false;
        } else if (!rightNeighbor.equals(other.rightNeighbor))
            return false;
        return true;
    }


    
}
