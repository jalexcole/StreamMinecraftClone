package io.mc.gameplay;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class CharacterController {
    float controllerBaseSpeed;
    float controllerRunSpeed;
    float movementSensitivity;
    float jumpForce;
    float downJumpForce;

    Vector3f cameraOffset;
    Vector3f movementAxis;
    Vector2f viewAxis;
    boolean isRunning;
    boolean lockedToCamera;
    boolean applyJumpForce;
    boolean inMiddleOfJump;

}
