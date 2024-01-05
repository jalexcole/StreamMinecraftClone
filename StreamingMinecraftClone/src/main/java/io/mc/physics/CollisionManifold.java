package io.mc.physics;

import org.joml.Vector3f;

record CollisionManifold(
        Vector3f overlap,
        CollisionFace face,
        boolean didCollide) {

}