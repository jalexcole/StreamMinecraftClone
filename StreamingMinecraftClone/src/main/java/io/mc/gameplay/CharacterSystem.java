package io.mc.gameplay;

import java.util.logging.Logger;

import org.joml.Vector2f;

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
            
            CharacterController controller = entity.comp2();
            Rigidbody rb = entity.comp3();
            controller.lockedToCamera = entity.equals(World.getLocalPlayer());


        });
    }


}
