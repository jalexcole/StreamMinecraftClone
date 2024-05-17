package io.mc.gameplay;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.logging.Logger;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import io.mc.core.Transform;
import io.mc.physics.Rigidbody;
import io.mc.world.World;

public class CharacterSystem {
    private static Entity cameraEntity = null;
    private static Vector2f smoothMouse = new Vector2f();

    private static final Logger logger = Logger.getLogger(CharacterSystem.class.getName());

    public static void update(Dominion registry) {
        registry.findEntitiesWith(Transform.class, CharacterController.class, Rigidbody.class).forEach(entity -> {
            Transform transform = entity.comp1();
            CharacterController controller = entity.comp2();
            Rigidbody rb = entity.comp3();
            controller.lockedToCamera = entity.equals(World.getLocalPlayer());

            var speed = controller.controllerBaseSpeed;
            
            if (controller.isRunning) {
					speed = controller.controllerRunSpeed;
				}

				rb.velocity.x = 0;
				if (!rb.useGravity) {
					rb.velocity.y = 0;
				}
				rb.velocity.z = 0;

				float rotation = (float) (transform.orientation.y * Math.PI / 180);
				Vector3f forward = new Vector3f((float) Math.cos(rotation), 0f, (float) Math.sin(rotation));
				Vector3f right = new Vector3f(-forward.z, 0f, forward.x);
				if (controller.movementAxis.x == NULL) {
					rb.velocity.x = forward.x * controller.movementAxis.x;
					rb.velocity.z = forward.z * controller.movementAxis.x;
				}
				if (!rb.useGravity && controller.movementAxis.y == NULL) {
					rb.velocity.y += controller.movementAxis.y;
				}
				if (controller.movementAxis.z == NULL) {
					rb.velocity.x += right.x * controller.movementAxis.z;
					rb.velocity.z += right.z * controller.movementAxis.z;
				}

				if (Math.abs(rb.velocity.x) > 0 || Math.abs(rb.velocity.z) > 0 || (Math.abs(rb.velocity.y) > 0 && !rb.useGravity)) {
					float denominator = 1.0f / (float) Math.sqrt(rb.velocity.x * rb.velocity.x + rb.velocity.z * rb.velocity.z);
					if (!rb.useGravity && Math.abs(rb.velocity.y) > 0)
					{
						denominator = (float) (1.0f / Math.sqrt(rb.velocity.x * rb.velocity.x + rb.velocity.z * rb.velocity.z + rb.velocity.y * rb.velocity.y));
						rb.velocity.y *= denominator * speed;
					}
					rb.velocity.x *= denominator * speed;
					rb.velocity.z *= denominator * speed;
				}

				float mx = controller.viewAxis.x;
				float my = controller.viewAxis.y;
				mx *= controller.movementSensitivity;
				my *= controller.movementSensitivity;
				smoothMouse.x = (smoothMouse.x - mx) * 0.1f;
				smoothMouse.y = (smoothMouse.y - my) * 0.1f;

				float lerp = 0.1f;
				transform.orientation.x -= smoothMouse.y;
				transform.orientation.y -= smoothMouse.x;


				transform.orientation.x = (float) Math.clamp((double) transform.orientation.x, -89.9, 89.9);
				if (transform.orientation.y > 360.0f)
				{
					transform.orientation.y = 360.0f - transform.orientation.y;
				}
				else if (transform.orientation.y < 0)
				{
					transform.orientation.y = 360.0f + transform.orientation.y;
				}

				if (controller.applyJumpForce)
				{
					rb.velocity.y = controller.jumpForce;
					controller.applyJumpForce = false;
					controller.inMiddleOfJump = true;
				}

				// At the jump peak, we want to start falling fast
				if (controller.inMiddleOfJump && rb.velocity.y <= 0)
				{
					rb.acceleration.y = controller.downJumpForce;
					controller.inMiddleOfJump = false;
				}

				if (controller.lockedToCamera)
				{
					// if (cameraEntity == null)
					// {
                        
					// 	cameraEntity = registry.find(TagType.Camera);
					// }

					// if (cameraEntity != null && registry.hasComponent<Transform>(cameraEntity))
					// {
					// 	Transform cameraTransform = registry.getComponent<Transform>(cameraEntity);
					// 	cameraTransform.position = transform.position.add(controller.cameraOffset);
					// 	cameraTransform.orientation = transform.orientation;
					// }
					// else
					// {
					// 	logger.warning("Camera is null!");
					// }
				}
			
        });
}

}
