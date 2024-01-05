package io.mc.core;

import org.joml.Vector3f;

import dev.dominion.ecs.api.Dominion;

public class TransformSystem {
    

    public static void update(Dominion registry) {
        registry.findEntitiesWith(PlayerComponent.class, Transform.class).forEach(entity -> {
            
            Transform transform = entity.comp2();
            
            Vector3f direction = new Vector3f();
            direction.x = (float) Math.cos(transform.orientation.y) * (float) Math.cos(transform.orientation.x);

        });
    }
}
