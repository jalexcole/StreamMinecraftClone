package io.mc.renderer;

import dev.dominion.ecs.api.Dominion;
import io.mc.core.Transform;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {
//    const glm::vec2 projectionSize = glm::vec2(6.0f, 3.0f);
    final Vector2f projectionSize = new Vector2f(6.0f, 3.0f);
//
//    glm::mat4 Camera::calculateViewMatrix(Ecs::Registry& registry) const
//    {
//        if (registry.hasComponent<Transform>(cameraEntity))
//        {
//            Transform& transform = registry.getComponent<Transform>(cameraEntity);
//
//            return glm::lookAt(
//                transform.position,
//                transform.position + transform.forward,
//                transform.up
//			);
//        }
//
//        return glm::mat4();
//    }
    public static Matrix4f calculateViewMatrix(Dominion registry) {
        throw new UnsupportedOperationException();
//        registry.findEntitiesWith(Transform.class).forEach(entity -> {
//
//            if(entity.entity().getName().equals("cameraEntity")) {
//                Transform transform = entity.comp();
//
//                // var lookAt = new Matrix4f(transform.position, transform.position.add(transform.forward), transform.up);
//            }
//        });
//
//        return new Matrix4f();
    }
//
//    glm::mat4 Camera::calculateProjectionMatrix(Ecs::Registry& registry) const
//    {
//        return glm::perspective(
//            glm::radians(fov),
//            Application::getWindow().getAspectRatio(),
//            0.1f,
//            2000.0f
//		);
//    };
    public static Matrix4f calculateProjectionMatrix(final Dominion registry) {
        throw new UnsupportedOperationException();
    }
//
//    glm::mat4 Camera::calculateHUDViewMatrix() const
//    {
//        return glm::lookAt(
//            glm::vec3(0, 0, 10),
//        glm::vec3(0, 0, 9),
//        glm::vec3(0, 1, 0)
//		);
//    }
    public static Matrix4f calculateHUDViewMatrix() {
        throw new UnsupportedOperationException();
    }

//
//    glm::mat4 Camera::calculateHUDProjectionMatrix() const
//    {
//        glm::vec2 halfSize = projectionSize / 2.0f;
//        return glm::ortho(-halfSize.x, halfSize.x, -halfSize.y, halfSize.y, -0.1f, 1000.0f);
//    }

    public static Matrix4f calculateHUDProjectionMatrix() {
        throw new UnsupportedOperationException();
    }


}
