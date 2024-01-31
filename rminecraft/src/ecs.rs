use glm::Vector3;

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
