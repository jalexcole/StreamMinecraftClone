package io.mc.world;

public class BlockFormat {
    TextureFormat sideTexture;
    TextureFormat topTexture;
    TextureFormat bottomTexture;
    String itemPictureName;
    boolean isTransparent;
    public boolean isSolid;
    boolean colorTopByBiome;
    boolean colorSideByBiome;
    boolean colorBottomByBiome;
    boolean isBlendable;
    boolean isLightSource;
    int lightLevel;
    boolean isItemOnly;
    boolean isStackable;
    int maxStackCount;
}
