package io.mc.world;

public class Block {
    public short id;
    short lightLevel;
    short lightColor;

    short compressedData;


    public Block(short id, short lightLevel, short lightColor, short compressedData) {
        this.id = id;
        this.lightLevel = lightLevel;
        this.lightColor = lightColor;
        this.compressedData = compressedData;
    }

    public boolean isItemOnly() {
        return false;
    }

    public boolean isTransparent() {
        // return (compressedData & (1 << 0));
        return false;
    }

    private void isBlendable() {

    }

    public void setIsBlendable(boolean isBendable) {

    }
}
