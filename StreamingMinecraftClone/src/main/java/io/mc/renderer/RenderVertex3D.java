package io.mc.renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderVertex3D {
    private Vector3f position;
    private int textureSlot;
    private Vector2f textureCoords;
    private Vector3f normal;
    /**
     * 
     */
    public RenderVertex3D() {
        this.position = new Vector3f();
        textureSlot = 0;
        textureCoords = new Vector2f();
        normal = new Vector3f();
    }
    /**
     * @param position
     * @param textureSlot
     * @param textureCoords
     * @param normal
     */
    public RenderVertex3D(Vector3f position, int textureSlot, Vector2f textureCoords, Vector3f normal) {
        this.position = position;
        this.textureSlot = textureSlot;
        this.textureCoords = textureCoords;
        this.normal = normal;
    }
    /**
     * @return the position
     */
    public Vector3f getPosition() {
        return position;
    }
    /**
     * @param position the position to set
     */
    public void setPosition(Vector3f position) {
        this.position = position;
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
    /**
     * @return the normal
     */
    public Vector3f getNormal() {
        return normal;
    }
    /**
     * @param normal the normal to set
     */
    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + textureSlot;
        result = prime * result + ((textureCoords == null) ? 0 : textureCoords.hashCode());
        result = prime * result + ((normal == null) ? 0 : normal.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RenderVertex3D))
            return false;
        RenderVertex3D other = (RenderVertex3D) obj;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (textureSlot != other.textureSlot)
            return false;
        if (textureCoords == null) {
            if (other.textureCoords != null)
                return false;
        } else if (!textureCoords.equals(other.textureCoords))
            return false;
        if (normal == null) {
            if (other.normal != null)
                return false;
        } else if (!normal.equals(other.normal))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "RenderVertex3D [position=" + position + ", textureSlot=" + textureSlot + ", textureCoords="
                + textureCoords + ", normal=" + normal + "]";
    }

    
}
