package io.mc.physics;

enum CollisionFace {
    NONE(0),
    TOP(1),
    BOTTOM(3),
    BACK(4),
    FRONT(5),
    LEFT(6),
    RIGHT(7);

    private final int value;

    private CollisionFace(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}