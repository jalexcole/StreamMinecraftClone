
pub struct Camera {
    fov: f32, // field of view,
}

impl Camera {


    fn calculate_HUD_view_matrix(&self) -> glm::Matrix4<f32>{
        todo!()
    }
}

pub struct Frustum {

}

impl Frustum {
    pub fn new() -> Self {
        todo!();
    }

    fn update(m: glm::Mat4) {

    }

    fn is_box_visible(minp: glm::Vec3, maxp: glm::Vec3) {

    }
}


pub struct FrameBuffer {
    fbo: u32,
    width: i32,
    height: i32,
    rbo: u32,
    include_depth_stencil: bool,
    color_attachments: Vec<Texture>
}

impl FrameBuffer {


    fn bind() {
        
    }

    fn unbind() {
            
        }
    fn regenerate() {

    }

    fn destroy() {

    }
}

enum Planes {
    Left = 0,
    Right,
    Bottom,
    Top,
    Near,
    Far,
    Count,
    Combinations
}


struct Shader {
    programId: u32,
    start_index: u32,
    filepath: String,
}

impl Shader {
    fn new() -> Self {
        Self::default()
    }

    fn compile(&mut self, shader_filepath: String) {
        self.filepath = shader_filepath.clone();
    }

    fn bind() {

    }

    fn destroy() {

    }


}


impl Default for Shader {
    fn default() -> Self {
        Self { programId: Default::default(), start_index: Default::default(), filepath: Default::default() }
    }
}

struct Texture {

}

