package io.mc.renderer;

public class VertexAttribute {
    private int attributeSlot = 0;
    private int numElements = 0;
    private AttributeType type = null;
    private int offset;
    /**
     * @param attributeSlot
     * @param numElements
     * @param type
     * @param offset
     */
    public VertexAttribute(int attributeSlot, int numElements, AttributeType type, int offset) {
        this.attributeSlot = attributeSlot;
        this.numElements = numElements;
        this.type = type;
        this.offset = offset;
    }
    /**
     * @return the attributeSlot
     */
    public int getAttributeSlot() {
        return attributeSlot;
    }
    /**
     * @param attributeSlot the attributeSlot to set
     */
    public void setAttributeSlot(int attributeSlot) {
        this.attributeSlot = attributeSlot;
    }
    /**
     * @return the numElements
     */
    public int getNumElements() {
        return numElements;
    }
    /**
     * @param numElements the numElements to set
     */
    public void setNumElements(int numElements) {
        this.numElements = numElements;
    }
    /**
     * @return the type
     */
    public AttributeType getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(AttributeType type) {
        this.type = type;
    }
    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }
    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + attributeSlot;
        result = prime * result + numElements;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + offset;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof VertexAttribute))
            return false;
        VertexAttribute other = (VertexAttribute) obj;
        if (attributeSlot != other.attributeSlot)
            return false;
        if (numElements != other.numElements)
            return false;
        if (type != other.type)
            return false;
        if (offset != other.offset)
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "VertexAttribute [attributeSlot=" + attributeSlot + ", numElements=" + numElements + ", type=" + type
                + ", offset=" + offset + "]";
    }

    
}
