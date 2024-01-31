use glm::{vec3, Vector3};

pub struct RaycastStaticResult {
    point: Vector3<f32>,
    block_center: Vector3<f32>,
    block_size: Vector3<f32>,
    hit_normal: Vector3<f32>,
    hit: bool,
}
impl RaycastStaticResult {
    fn default() -> RaycastStaticResult {
        return RaycastStaticResult {
            point: vec3(0.0, 0.0, 0.0),
            block_center: vec3(0.0, 0.0, 0.0),
            block_size: vec3(0.0, 0.0, 0.0),
            hit_normal: vec3(0.0, 0.0, 0.0),
            hit: false,
        };
    }
}

pub struct Rigidbody {
    velocity: Vector3<f32>,
    acceleration: Vector3<f32>,
    on_ground: bool,
    is_sensor: bool,
    use_gravity: bool,
}

impl Rigidbody {
    fn zero_forces(&mut self) {
        self.acceleration = vec3(0.0, 0.0, 0.0);
        self.velocity = vec3(0.0, 0.0, 0.0);
    }
}

pub struct BoxCollider {
    size: Vector3<f32>,
    offset: Vector3<f32>,
}

pub enum CollisionFace {
    NONE = 0,
    TOP = 1,
    BOTTOM = 2,
    BACK = 3,
    FRONT = 4,
    LEFT = 5,
    RIGHT = 6,
}

struct Interval {
    min: f32,
    max: f32,
}

impl Interval {
    fn default() -> Interval {
        Interval {min: 0.0,max:  0.0}
    }
}

struct CollisionManifold {
    overlap: Vector3<f32>,
    face: CollisionFace,
    did_collide: bool,
}

pub mod Physics {
    use std::ops::Add;
    use std::ops::Mul;

    use crate::ecs::Transform;

    use super::BoxCollider;
    use super::CollisionFace;
    use super::Interval;
    use super::RaycastStaticResult;
    use glm::vec3;
    use glm::ApproxEq;
    use glm::Vector3;

    const GRAVITY: Vector3<f32> = Vector3::<f32> {
        x: 0.0,
        y: 20.0,
        z: 0.0,
    };
    const TERMINAL_VELOCITY: Vector3<f32> = Vector3::<f32> {
        x: 50.0,
        y: 50.0,
        z: 50.0,
    };

    const PHYSICS_UPDATE_RATE: f32 = 1.0 / 120.0;

    fn init() {}

    fn update(registry: shipyard::World) {
        //    {static mut accumulated_delta_time: Lazy<f32> = Lazy::new(0.0);

        //     // accumulated_delta_time = 0.0;
        //     }
    }

    fn raycastStatic(
        origin: &Vector3<f32>,
        normal_direction: &Vector3<f32>,
        max_distance: f32,
        draw: bool,
        block_corner: &Vector3<f32>,
        step: Vector3<f32>,
    ) -> RaycastStaticResult {
        let mut result: RaycastStaticResult = RaycastStaticResult::default();

        result.hit = false;

        if (normal_direction.is_approx_eq(&vec3(0.0, 0.0, 0.0))) {
            return result;
        }

        if (draw) {}

        let ray_end = origin.add(normal_direction.mul(max_distance));

        todo!()
    }

    fn resolve_static_collision() {}

    fn static_collision_information() {}

    fn get_direction(face: &CollisionFace) -> f32 {
        match face {
            CollisionFace::NONE => return -1.0,
            CollisionFace::TOP => todo!(),
            CollisionFace::BOTTOM => todo!(),
            CollisionFace::BACK => todo!(),
            CollisionFace::FRONT => todo!(),
            CollisionFace::LEFT => todo!(),
            CollisionFace::RIGHT => todo!(),
        }
    }

    fn get_quadrant_result() {}

    fn is_colliding() {}

    fn penetration_amount() {}

    fn get_interval(
        box_collider: &BoxCollider,
        transform: &Transform,
        axis: &Vector3<f32>,
    ) -> Interval {
        let mut vertices = Vec::new();

        let center = transform.position;
        let half_size = box_collider.size * 0.5;

        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z)
                + vec3(0.0, 1.0, 0.0).mul(half_size.y)
                + vec3(1.0, 0.0, 0.0).mul(half_size.x),
        ));
        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z) + vec3(0.0, 1.0, 0.0) * (half_size.y)
                - vec3(1.0, 0.0, 0.0) * (half_size.x),
        ));
        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z)
                + vec3(0.0, 1.0, 0.0).mul(half_size.y)
                + vec3(1.0, 0.0, 0.0).mul(half_size.x),
        ));
        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z)
                + vec3(0.0, 1.0, 0.0).mul(half_size.y)
                + vec3(1.0, 0.0, 0.0).mul(half_size.x),
        ));
        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z)
                + vec3(0.0, 1.0, 0.0).mul(half_size.y)
                + vec3(1.0, 0.0, 0.0).mul(half_size.x),
        ));
        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z)
                + vec3(0.0, 1.0, 0.0).mul(half_size.y)
                + vec3(1.0, 0.0, 0.0).mul(half_size.x),
        ));
        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z)
                + vec3(0.0, 1.0, 0.0).mul(half_size.y)
                + vec3(1.0, 0.0, 0.0).mul(half_size.x),
        ));
        vertices.push(center.add(
            vec3(0.0, 0.0, 1.0) * (half_size.z)
                + vec3(0.0, 1.0, 0.0).mul(half_size.y)
                + vec3(1.0, 0.0, 0.0).mul(half_size.x),
        ));

        let result = Interval::default();
        // let dot_product = axis.dot( &vertices[0]);

        for vertex in vertices {
            
        }

        todo!()
    }

    fn close_point_on_ray() {}
}
