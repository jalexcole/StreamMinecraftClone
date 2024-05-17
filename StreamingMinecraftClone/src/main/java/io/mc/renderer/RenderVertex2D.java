package io.mc.renderer;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class RenderVertex2D {
    Vector2f position;
    Vector4f color;
    int textureSlot;
    Vector2f textureCoords;

    public RenderVertex2D() {
        this.position = new Vector2f();
        this.color = new Vector4f();
        this.textureCoords = new Vector2f();
        this.textureSlot = 0;
    }

    /**
     * @param position
     * @param color
     * @param textureSlot
     * @param textureCoords
     */
    public RenderVertex2D(Vector2f position, Vector4f color, int textureSlot, Vector2f textureCoords) {
        this.position = position;
        this.color = color;
        this.textureSlot = textureSlot;
        this.textureCoords = textureCoords;
    }

    /**
     * @return the position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Vector2f position) {
        this.position = position;
    }

    /**
     * @return the color
     */
    public Vector4f getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Vector4f color) {
        this.color = color;
    }

    /**
     * @return the textureSlot
     */
    public int getTextureSlot() {
        return textureSlot;
    }

    /**
     * @param textureSlot the textureSlot to set
     */
    public void setTextureSlot(int textureSlot) {
        this.textureSlot = textureSlot;
    }

    /**
     * @return the textureCoords
     */
    public Vector2f getTextureCoords() {
        return textureCoords;
    }

    /**
     * @param textureCoords the textureCoords to set
     */
    public void setTextureCoords(Vector2f textureCoords) {
        this.textureCoords = textureCoords;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + textureSlot;
        result = prime * result + ((textureCoords == null) ? 0 : textureCoords.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RenderVertex2D))
            return false;
        RenderVertex2D other = (RenderVertex2D) obj;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (color == null) {
            if (other.color != null)
                return false;
        } else if (!color.equals(other.color))
            return false;
        if (textureSlot != other.textureSlot)
            return false;
        if (textureCoords == null) {
            if (other.textureCoords != null)
                return false;
        } else if (!textureCoords.equals(other.textureCoords))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RenderVertex2D [position=" + position + ", color=" + color + ", textureSlot=" + textureSlot
                + ", textureCoords=" + textureCoords + "]";
    }

    

    
}