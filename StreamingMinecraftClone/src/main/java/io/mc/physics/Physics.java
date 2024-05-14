package io.mc.physics;

import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import io.mc.core.Transform;
import io.mc.renderer.Renderer;
import io.mc.renderer.Style;
import io.mc.renderer.Styles;
import io.mc.utils.JMath;
import io.mc.world.Block;
import io.mc.world.BlockFormat;
import io.mc.world.BlockMap;
import io.mc.world.ChunkManager;
import io.mc.world.World;

public final class Physics {
    private static Vector3f gravity = new Vector3f(0, 20.0f, 0.0f);
    private static Vector3f terminalVelocity = new Vector3f(50.0f, 50.0f, 50.0f);
    private static final float physicsUpdateRate = 1.0f / 120.0f;

    public static void init() {

    }

    public static void update(@NonNull Dominion registry) {
        float accumulatedDeltaTime = 0.0f;
        accumulatedDeltaTime += World.getDeltaTime();

        int numUpdates = 0;

        while (accumulatedDeltaTime >= physicsUpdateRate) {
            registry.findEntitiesWith(Transform.class, Rigidbody.class, BoxCollider.class).forEach(entity -> {
                Rigidbody rb = entity.comp2();
                Transform transform = entity.comp1();
                BoxCollider boxCollider = entity.comp3();

                transform.position.add(rb.velocity.mul(physicsUpdateRate));
                rb.velocity.add(rb.acceleration.mul(physicsUpdateRate));

                if (rb.useGravity) {
                    // rb.velocity.y -= Math.clamp(rb.velocity.y, -1 * terminalVelocity.y,
                    // terminalVelocity.y);
                }
            });
        }
    }

    public static RaycastStaticResult raycastStatic(@NonNull Vector3f origin, @NonNull Vector3f normalDirection,
            float maxDistance,
            boolean draw) {
        RaycastStaticResult result = new RaycastStaticResult();
        result.hit = false;

        if (normalDirection.equals(new Vector3f())) {
            return result;
        }

        Style red = Styles.defaultStyle;
        red.color = 0xE90101;
        if (draw) {
            Renderer.drawLine(origin, origin.add(normalDirection.mul(maxDistance)), red);
        }

        // NOTE: Thank God for this paper
        // http://www.cse.yorku.ca/~amana/research/grid.pdf which outlines what I'm
        // doing here
        Vector3f rayEnd = origin.add(normalDirection.mul(maxDistance));
        // Do some fancy math to figure out which voxel is the next voxel
        Vector3f blockCenter = origin.ceil();
        Vector3f step = normalDirection.absolute();
        // Max amount we can step in any direction of the ray, and remain in the voxel
        Vector3f blockCenterToOriginSign = (blockCenter.sub(origin)).normalize();
        Vector3f goodNormalDirection = new Vector3f(
                normalDirection.x == 0.0f ? 1e-10f * blockCenterToOriginSign.x : normalDirection.x,
                normalDirection.y == 0.0f ? 1e-10f * blockCenterToOriginSign.y : normalDirection.y,
                normalDirection.z == 0.0f ? 1e-10f * blockCenterToOriginSign.z : normalDirection.z);
        Vector3f tDelta = ((blockCenter.add(step)).sub(origin)).div(goodNormalDirection);
        // If any number is 0, then we max the delta so we don't get a false positive
        if (tDelta.x == 0.0f)
            tDelta.x = 1e10f;
        if (tDelta.y == 0.0f)
            tDelta.y = 1e10f;
        if (tDelta.z == 0.0f)
            tDelta.z = 1e10f;
        Vector3f tMax = tDelta;
        float minTValue;
        do {
            // TODO: This shouldn't have to be calculated every step
            tDelta = (blockCenter.sub(origin)).div(goodNormalDirection);
            tMax = tDelta;
            // minTValue = FLT_MAX;
            if (tMax.x < tMax.y) {
                if (tMax.x < tMax.z) {
                    blockCenter.x += step.x;
                    // Check if we actually hit the block
                    if (doRaycast(origin, normalDirection, maxDistance, draw, blockCenter, step, result)) {
                        return result;
                    }
                    // tMax.x += tDelta.x;
                    minTValue = tMax.x;
                } else {
                    blockCenter.z += step.z;
                    if (doRaycast(origin, normalDirection, maxDistance, draw, blockCenter, step, result)) {
                        return result;
                    }
                    // tMax.z += tDelta.z;
                    minTValue = tMax.z;
                }
            } else {
                if (tMax.y < tMax.z) {
                    blockCenter.y += step.y;
                    if (doRaycast(origin, normalDirection, maxDistance, draw, blockCenter, step, result)) {
                        return result;
                    }
                    // tMax.y += tDelta.y;
                    minTValue = tMax.y;
                } else {
                    blockCenter.z += step.z;
                    if (doRaycast(origin, normalDirection, maxDistance, draw, blockCenter, step, result)) {
                        return result;
                    }
                    // tMax.z += tDelta.z;
                    minTValue = tMax.z;
                }
            }
        } while (minTValue < maxDistance);

        return result;
    }

    private static boolean doRaycast(Vector3f origin, Vector3f normalDirection, float maxDistance, boolean draw,
            Vector3f blockCorner,
            Vector3f step, RaycastStaticResult out) {
        Vector3f blockCenter = blockCorner.min((new Vector3f(0.5f).mul(step)));
        if (draw) {
            Renderer.drawBox(blockCenter, new Vector3f(1.0f, 1.0f, 1.0f), Styles.defaultStyle);
        }

        int blockId = ChunkManager.getBlock(blockCenter).id;
        // BlockFormat block = BlockMap.getBlock(blockId);
        if (blockId != BlockMap.NULL_BLOCK.id && blockId != BlockMap.AIR_BLOCK.id) {
            BoxCollider currentBox = new BoxCollider();
            currentBox.offset = new Vector3f();
            currentBox.size = new Vector3f(1.0f, 1.0f, 1.0f);
            Transform currentTransform = new Transform();
            currentTransform.position = blockCenter;

            Block block = ChunkManager.getBlock(currentTransform.position);
            BlockFormat blockFormat = BlockMap.getBlock(block.id);

            if (blockFormat.isSolid) {
                Vector3f min = currentTransform.position.sub((currentBox.size.mul(0.5f)).add(currentBox.offset));
                Vector3f max = currentTransform.position.add(currentBox.size.mul(0.5f)).add(currentBox.offset);
                float t1 = (min.x - origin.x)
                        / (JMath.compare(normalDirection.x, 0.0f, Float.MIN_VALUE) ? 0.00001f : normalDirection.x);
                float t2 = (max.x - origin.x)
                        / (JMath.compare(normalDirection.x, 0.0f, Float.MIN_VALUE) ? 0.00001f : normalDirection.x);
                float t3 = (min.y - origin.y)
                        / (JMath.compare(normalDirection.y, 0.0f, Float.MIN_VALUE) ? 0.00001f : normalDirection.y);
                float t4 = (max.y - origin.y)
                        / (JMath.compare(normalDirection.y, 0.0f, Float.MIN_VALUE) ? 0.00001f : normalDirection.y);
                float t5 = (min.z - origin.z)
                        / (JMath.compare(normalDirection.z, 0.0f, Float.MIN_VALUE) ? 0.00001f : normalDirection.z);
                float t6 = (max.z - origin.z)
                        / (JMath.compare(normalDirection.z, 0.0f, Float.MIN_VALUE) ? 0.00001f : normalDirection.z);

                float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
                float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
                if (tmax < 0 || tmin > tmax) {
                    // No intersection
                    return false;
                }

                float depth = 0.0f;
                if (tmin < 0.0f) {
                    // The ray's origin is inside the AABB
                    depth = tmax;
                } else {
                    depth = tmin;
                }

                out.point = origin.add(normalDirection.mul(depth));
                out.hit = true;
                out.blockCenter = currentTransform.position.add(currentBox.offset);
                out.blockSize = currentBox.size;
                out.hitNormal = out.point.sub(out.blockCenter);
                float maxComponent = Math.max(Math.abs(out.hitNormal.x),
                        Math.max(Math.abs(out.hitNormal.y), Math.abs(out.hitNormal.z)));
                out.hitNormal = Math.abs(out.hitNormal.x) == maxComponent
                        ? new Vector3f(1, 0, 0).mul(Math.signum(out.hitNormal.x))
                        : Math.abs(out.hitNormal.y) == maxComponent
                                ? new Vector3f(0, 1, 0).mul(Math.signum(out.hitNormal.y))
                                : new Vector3f(0, 0, 1).mul(Math.signum(out.hitNormal.z));
                return true;
            }
        }

        return false;
    }

    private static void resolveStaticCollision(Entity entity, Rigidbody rb, Transform transform,
            BoxCollider boxCollider) {
        throw new UnsupportedOperationException();
    }

    private static CollisionManifold staticCollisionInformation(Rigidbody r1, BoxCollider b1, Transform t1,
            BoxCollider b2, Transform t2) {
        throw new UnsupportedOperationException();
    }

    private static float getDirection(CollisionFace face) {
        switch(face) {
            case BACK:
                return 1f;
            case BOTTOM:
                return 1f;
            case FRONT:
                return -1.0f;
            case LEFT:
                return 1.0f;
            case NONE:
                return 0.0f;
            case RIGHT:
                return -1.0f;
            case TOP:
                return -1.0f;
            default:
                return 0.0f;

        }
        
    }

    private static void getQuadrantResult(final Transform t1,final Transform t2,final BoxCollider b2Expanded, final CollisionManifold res) {
        throw new UnsupportedOperationException();
    }

    private static float peneratrationAmount() {
        throw new UnsupportedOperationException();
    }

    private static Interval getInterval() {
        throw new UnsupportedOperationException();
    }

}