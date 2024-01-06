package io.mc.utils;

import org.joml.Vector2i;

public class INormals2 {
    public static final Vector2i Up = new Vector2i(1, 0);
    public static final Vector2i Down = new Vector2i(-1, 0);
    public static final Vector2i Left = new Vector2i(0, -1);
    public static final Vector2i Right = new Vector2i(0, 1);

    public static final Vector2i[] CardinalDirections = { Up, Down, Left, Right };
}
