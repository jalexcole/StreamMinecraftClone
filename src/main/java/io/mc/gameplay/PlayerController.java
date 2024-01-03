package io.mc.gameplay;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import io.mc.core.Transform;
import io.mc.physics.Rigidbody;

public class PlayerController {
    public static boolean generateCubemap = false;
    private static CubemapSide sideGenerating = CubemapSide.LEFT;

    private static Entity playerId = null;



    public static void init() {

    }

    public static void update(Dominion registry) {
        setPlayerIdNeeded();
    }

    private static void setPlayerIdNeeded() {
    }

    private static void updateSurvival(Transform transform, CharacterController controller, Rigidbody rb,
            Inventory inventory) {

    }
    
    private static void updateCreative(Transform transform, CharacterController controller, Rigidbody rb,
            Inventory inventory) {

    }
    
    private static void updateSpectator(Transform transform, CharacterController controller, Rigidbody rb) {

    }

    private static void updateInventory(Inventory inventory) {
        
    }
}
