package io.mc.physics;

import org.joml.Vector3f;

class RaycastStaticResult {
    Vector3f point = new Vector3f();
    Vector3f blockCenter = new Vector3f();
    Vector3f blockSize = new Vector3f();
    Vector3f hitNormal = new Vector3f();
    boolean hit = false;
}