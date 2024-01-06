package io.mc.network;

import org.joml.Vector3f;

import dev.dominion.ecs.api.Entity;

public class UpdateTransformCommand {
    long timestamp;
    Entity entity;
    Vector3f position;
    Vector3f rotation;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        result = prime * result + ((entity == null) ? 0 : entity.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((rotation == null) ? 0 : rotation.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof UpdateTransformCommand))
            return false;
        UpdateTransformCommand other = (UpdateTransformCommand) obj;
        if (timestamp != other.timestamp)
            return false;
        if (entity == null) {
            if (other.entity != null)
                return false;
        } else if (!entity.equals(other.entity))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (rotation == null) {
            if (other.rotation != null)
                return false;
        } else if (!rotation.equals(other.rotation))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "UpdateTransformCommand [timestamp=" + timestamp + ", entity=" + entity + ", position=" + position
                + ", rotation=" + rotation + "]";
    }

    
    
}
