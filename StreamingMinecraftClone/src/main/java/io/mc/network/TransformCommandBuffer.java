package io.mc.network;

import java.util.List;

public class TransformCommandBuffer {
    List<UpdateTransformCommand> buffer;

    int size;
    int maxSize;

    void init(int maxNumPositionCommands) {

    }

    void free() {

    }

    /**
     * @return the buffer
     */
    public List<UpdateTransformCommand> getBuffer() {
        return buffer;
    }

    /**
     * @param buffer the buffer to set
     */
    public void setBuffer(List<UpdateTransformCommand> buffer) {
        this.buffer = buffer;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the maxSize
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @param maxSize the maxSize to set
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
        result = prime * result + size;
        result = prime * result + maxSize;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TransformCommandBuffer))
            return false;
        TransformCommandBuffer other = (TransformCommandBuffer) obj;
        if (buffer == null) {
            if (other.buffer != null)
                return false;
        } else if (!buffer.equals(other.buffer))
            return false;
        if (size != other.size)
            return false;
        if (maxSize != other.maxSize)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TransformCommandBuffer [buffer=" + buffer + ", size=" + size + ", maxSize=" + maxSize + "]";
    }

    
    
}
