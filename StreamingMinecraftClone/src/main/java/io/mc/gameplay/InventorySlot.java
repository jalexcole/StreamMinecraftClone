package io.mc.gameplay;

public class InventorySlot {
    short blockId;
    byte count;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + blockId;
        result = prime * result + count;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof InventorySlot))
            return false;
        InventorySlot other = (InventorySlot) obj;
        if (blockId != other.blockId)
            return false;
        if (count != other.count)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InventorySlot [blockId=" + blockId + ", count=" + count + "]";
    }
}
