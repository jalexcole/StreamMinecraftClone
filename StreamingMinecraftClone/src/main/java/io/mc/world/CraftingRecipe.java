package io.mc.world;

public class CraftingRecipe {
    int maxWidth;
    int maxHeight;
    // 9 is the max crafting size
    short[] blockIds = new short[9];
    short output;
    byte outputCount;
}
