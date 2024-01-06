package io.mc.physics;

import org.joml.Vector3f;

public class BoxCollider {
    Vector3f size = new Vector3f();
    Vector3f offset = new Vector3f();

    public BoxCollider() {

    }

    public BoxCollider(Vector3f size, Vector3f offset) {
        this.size = size;
        this.offset = offset;
    }

    /**
     * @return the size
     */
    public Vector3f getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Vector3f size) {
        this.size = size;
    }

    /**
     * @return the offset
     */
    public Vector3f getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof BoxCollider))
            return false;
        BoxCollider other = (BoxCollider) obj;
        if (size == null) {
            if (other.size != null)
                return false;
        } else if (!size.equals(other.size))
            return false;
        if (offset == null) {
            if (other.offset != null)
                return false;
        } else if (!offset.equals(other.offset))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BoxCollider [size=" + size + ", offset=" + offset + "]";
    }

}
