package io.mc.physics;

import org.joml.Vector3f;

class RaycastStaticResult {
    Vector3f point = new Vector3f();
    Vector3f blockCenter = new Vector3f();
    Vector3f blockSize = new Vector3f();
    Vector3f hitNormal = new Vector3f();
    boolean hit = false;

    public RaycastStaticResult() {

    }

    public RaycastStaticResult(Vector3f point, Vector3f blockCenter) {
        this.point = point;
        this.blockCenter = blockCenter;
    }

    /**
     * @return the point
     */
    public Vector3f getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(Vector3f point) {
        this.point = point;
    }

    /**
     * @return the blockCenter
     */
    public Vector3f getBlockCenter() {
        return blockCenter;
    }

    /**
     * @param blockCenter the blockCenter to set
     */
    public void setBlockCenter(Vector3f blockCenter) {
        this.blockCenter = blockCenter;
    }

    /**
     * @return the blockSize
     */
    public Vector3f getBlockSize() {
        return blockSize;
    }

    /**
     * @param blockSize the blockSize to set
     */
    public void setBlockSize(Vector3f blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * @return the hitNormal
     */
    public Vector3f getHitNormal() {
        return hitNormal;
    }

    /**
     * @param hitNormal the hitNormal to set
     */
    public void setHitNormal(Vector3f hitNormal) {
        this.hitNormal = hitNormal;
    }

    /**
     * @return the hit
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * @param hit the hit to set
     */
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((point == null) ? 0 : point.hashCode());
        result = prime * result + ((blockCenter == null) ? 0 : blockCenter.hashCode());
        result = prime * result + ((blockSize == null) ? 0 : blockSize.hashCode());
        result = prime * result + ((hitNormal == null) ? 0 : hitNormal.hashCode());
        result = prime * result + (hit ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RaycastStaticResult))
            return false;
        RaycastStaticResult other = (RaycastStaticResult) obj;
        if (point == null) {
            if (other.point != null)
                return false;
        } else if (!point.equals(other.point))
            return false;
        if (blockCenter == null) {
            if (other.blockCenter != null)
                return false;
        } else if (!blockCenter.equals(other.blockCenter))
            return false;
        if (blockSize == null) {
            if (other.blockSize != null)
                return false;
        } else if (!blockSize.equals(other.blockSize))
            return false;
        if (hitNormal == null) {
            if (other.hitNormal != null)
                return false;
        } else if (!hitNormal.equals(other.hitNormal))
            return false;
        if (hit != other.hit)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RaycastStaticResult [point=" + point + ", blockCenter=" + blockCenter + ", blockSize=" + blockSize
                + ", hitNormal=" + hitNormal + ", hit=" + hit + "]";
    }
    
    

}