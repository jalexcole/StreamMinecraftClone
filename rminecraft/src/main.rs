use rminecraft::physics::Rigidbody;

fn main() {
    println!("Hello, world!");

    let mut app = Application::new();
    app.run();
}



struct Application {
    delta_time: f32
}

impl Application {

    fn new() -> Self {
        Self { delta_time: 0.0 }
    }
    fn init(&mut self) {

    }

    fn run(&mut self) {

    }
}
