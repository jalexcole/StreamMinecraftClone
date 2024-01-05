package io.mc.renderer;

public enum TextureType {
    None(0),
    ONE_D(1),
    TWO_D(2),
    _CUBEMAP(3),
    _CUBEMAP_POSITIVE_X(4),
    _CUBEMAP_NEGATIVE_X(5),
    _CUBEMAP_POSITIVE_Y(6),
    _CUBEMAP_NEGATIVE_Y(7),
    _CUBEMAP_POSITIVE_Z(8),
    _CUBEMAP_NEGATIVE_Z(9);

    private final int value;

    private TextureType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
