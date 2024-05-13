

pub mod Application {
    fn init() {}

    fn run() {}

    fn free() {}
}

pub mod TransformSystem {
    use std::ops::RangeBounds;

    fn update(registry: &shipyard::World) {}
}

pub enum GEventType {
    None = 0,
    SetDeltaTime,
    PlayerKeyInput,
    PlayerMouseInput,
    PlayerCharInput,
    PlayerMouseButtonInput,
    PlayerMouseScrollInput,
    MouseInitial,
    SetPlayerPos,
    SetPlayerViewAxis,
    SetPlayerOrientation,
    SetPlayerForward,
    FrameTick,
}

pub struct GEvent {
    event_type: GEventType,
    size: usize,
    data: Box<dyn std::any::Any>,
    free_data: bool,
}

pub trait Scene {
    

    fn init(scene_type: SceneType, registry: shipyard::World) {

    }

    fn update() {
        
    }
}



pub enum SceneType {
    None,
    SinglePlayerGame,
    LocalLanGame,
    MultiplayerGame,
    MainMenu,
    Replay,
}

fn resize_callback(window_ptr: &mut glfw::Window, new_width: i32, new_height: i32) {}

pub struct Window {

}

impl Window {

    fn create(title: String) {
        use glfw::fail_on_errors;
        let mut glfw = glfw::init(fail_on_errors!()).unwrap();
    }

}
