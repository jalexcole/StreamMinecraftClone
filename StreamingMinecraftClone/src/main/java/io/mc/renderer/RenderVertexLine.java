package io.mc.renderer;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderVertexLine {
    Vector3f start = new Vector3f();
    Vector3f end = new Vector3f();
    float isStart = 0.0f;
    float direction = 0;
    float strokeWidth = 0.0f;
    Vector4f color = new Vector4f();

    public RenderVertexLine() {

    }

    /**
     * @param start
     * @param end
     * @param isStart
     * @param direction
     * @param strokeWidth
     * @param color
     */
    public RenderVertexLine(Vector3f start, Vector3f end, float isStart, float direction, float strokeWidth,
            Vector4f color) {
        this.start = start;
        this.end = end;
        this.isStart = isStart;
        this.direction = direction;
        this.strokeWidth = strokeWidth;
        this.color = color;
    }

    /**
     * @return the start
     */
    public Vector3f getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Vector3f start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Vector3f getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Vector3f end) {
        this.end = end;
    }

    /**
     * @return the isStart
     */
    public float getIsStart() {
        return isStart;
    }

    /**
     * @param isStart the isStart to set
     */
    public void setIsStart(float isStart) {
        this.isStart = isStart;
    }

    /**
     * @return the direction
     */
    public float getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(float direction) {
        this.direction = direction;
    }

    /**
     * @return the strokeWidth
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * @param strokeWidth the strokeWidth to set
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * @return the color
     */
    public Vector4f getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Vector4f color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + Float.floatToIntBits(isStart);
        result = prime * result + Float.floatToIntBits(direction);
        result = prime * result + Float.floatToIntBits(strokeWidth);
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RenderVertexLine))
            return false;
        RenderVertexLine other = (RenderVertexLine) obj;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (Float.floatToIntBits(isStart) != Float.floatToIntBits(other.isStart))
            return false;
        if (Float.floatToIntBits(direction) != Float.floatToIntBits(other.direction))
            return false;
        if (Float.floatToIntBits(strokeWidth) != Float.floatToIntBits(other.strokeWidth))
            return false;
        if (color == null) {
            if (other.color != null)
                return false;
        } else if (!color.equals(other.color))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RenderVertexLine [start=" + start + ", end=" + end + ", isStart=" + isStart + ", direction=" + direction
                + ", strokeWidth=" + strokeWidth + ", color=" + color + "]";
    }

    

    
}