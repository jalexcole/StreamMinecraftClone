package io.mc.renderer;

enum Planes {
    LEFT(0),
    RIGHT(1),
    BOTTOM(2),
    TOP(3),
    NEAR(4),
    FAR(5),
    COUNT(6),
    COMBINATIONS(15);

    private int value;

    private Planes(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    
}