package io.mc.physics;

import org.joml.Vector3f;

public class Rigidbody {
    Vector3f velocity = new Vector3f();
    Vector3f acceleration = new Vector3f();

    boolean onGround = true;
    boolean isSensor = true;
    boolean useGravity = true;

    public void zeroForces() {
        this.acceleration = new Vector3f();
        velocity = new Vector3f();
    }

    /**
     * @return the velocity
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    /**
     * @return the acceleration
     */
    public Vector3f getAcceleration() {
        return acceleration;
    }

    /**
     * @param acceleration the acceleration to set
     */
    public void setAcceleration(Vector3f acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * @return the onGround
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * @param onGround the onGround to set
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    /**
     * @return the isSensor
     */
    public boolean isSensor() {
        return isSensor;
    }

    /**
     * @param isSensor the isSensor to set
     */
    public void setSensor(boolean isSensor) {
        this.isSensor = isSensor;
    }

    /**
     * @return the useGravity
     */
    public boolean isUseGravity() {
        return useGravity;
    }

    /**
     * @param useGravity the useGravity to set
     */
    public void setUseGravity(boolean useGravity) {
        this.useGravity = useGravity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((velocity == null) ? 0 : velocity.hashCode());
        result = prime * result + ((acceleration == null) ? 0 : acceleration.hashCode());
        result = prime * result + (onGround ? 1231 : 1237);
        result = prime * result + (isSensor ? 1231 : 1237);
        result = prime * result + (useGravity ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Rigidbody))
            return false;
        Rigidbody other = (Rigidbody) obj;
        if (velocity == null) {
            if (other.velocity != null)
                return false;
        } else if (!velocity.equals(other.velocity))
            return false;
        if (acceleration == null) {
            if (other.acceleration != null)
                return false;
        } else if (!acceleration.equals(other.acceleration))
            return false;
        if (onGround != other.onGround)
            return false;
        if (isSensor != other.isSensor)
            return false;
        if (useGravity != other.useGravity)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Rigidbody [velocity=" + velocity + ", acceleration=" + acceleration + ", onGround=" + onGround
                + ", isSensor=" + isSensor + ", useGravity=" + useGravity + "]";
    }

    
}
