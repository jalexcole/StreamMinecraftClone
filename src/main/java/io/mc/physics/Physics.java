package io.mc.physics;

import org.joml.Vector3f;

public class Physics {

}

record Interval(float min, float max) {
}

enum CollisionFace {
    NONE,
    TOP,
    BOTTOM,
    BACK,
    FRONT,
    LEFT,
    RIGHT
}

record CollisionManifold(
        Vector3f overlap,
        CollisionFace face,
        boolean didCollide) {

}