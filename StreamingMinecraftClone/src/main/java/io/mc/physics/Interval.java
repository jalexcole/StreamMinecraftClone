package io.mc.physics;

class Interval {
    float min, max;

    public Interval(float min, float max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @return the min
     */
    public float getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(float min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public float getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(float max) {
        this.max = max;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(min);
        result = prime * result + Float.floatToIntBits(max);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Interval))
            return false;
        Interval other = (Interval) obj;
        if (Float.floatToIntBits(min) != Float.floatToIntBits(other.min))
            return false;
        else if (Float.floatToIntBits(max) != Float.floatToIntBits(other.max))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Interval [min=" + min + ", max=" + max + "]";
    }
}