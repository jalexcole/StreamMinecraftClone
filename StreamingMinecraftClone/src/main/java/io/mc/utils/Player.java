package io.mc.utils;

public class Player {
    public static final int numHotbarSlots = 9;
    public static final int numMainInventoryRows = 3;
    public static final int numMainInventoryColumns = 9;
    public static final int numMainInventorySlots = numMainInventoryRows * numMainInventoryColumns;
    public static final int numTotalSlots = numMainInventorySlots + numHotbarSlots;
}
