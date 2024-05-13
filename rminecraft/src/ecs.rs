use glm::{vec3, Vector3};

pub enum TagType {
    None,
    Player,
    Camera,
}

pub struct Tag {
    tag_type: TagType,
}

pub struct Transform {
    pub position: Vector3<f32>,
    scale: Vector3<f32>,
    orientation: Vector3<f32>,
    forward: Vector3<f32>,
    up: Vector3<f32>,
    gright: Vector3<f32>,
}
impl Default for Transform {
    fn default() -> Self {
        Transform {
            position: vec3(0.0, 0.0, 0.0),
            scale: todo!(),
            orientation: todo!(),
            forward: todo!(),
            up: todo!(),
            gright: todo!(),
        }
    }
}
