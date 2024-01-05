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
}
