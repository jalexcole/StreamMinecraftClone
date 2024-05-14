package io.mc.utils;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class JMath {
    static float PI = 3.1415926535897932384626433832795028841971693993751058209749445923078164062f;

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    static boolean compare(final Vector2f vec1, final Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(final Vector3f vec1, final Vector3f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon) && compare(vec1.z, vec2.z, epsilon);
    }

    public static boolean compare(final Vector4f vec1, final Vector4f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon) && compare(vec1.z, vec2.z, epsilon)
                && compare(vec1.w, vec2.w, epsilon);
    }

    public Vector2f vector2From3(final Vector3f vec) {
        return new Vector2f(vec.x, vec.y);
    }

    Vector3f vector3From2(final Vector2f vec) {
        return new Vector3f(vec.x, vec.y, 0.0f);
    }

    float toRadians(float degrees) {
        return degrees * PI / 180.0f;
    }

    float toDegrees(float radians) {
        return radians * 180.0f / PI;
    }

    void rotate(Vector2f vec, float angleDeg, final Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float xPrime = origin.x
                + ((x * (float) Math.cos(toRadians(angleDeg))) - (y * (float) Math.sin(toRadians(angleDeg))));
        float yPrime = origin.y
                + ((x * (float) Math.sin(toRadians(angleDeg))) + (y * (float) Math.cos(toRadians(angleDeg))));

        vec.x = xPrime;
        vec.y = yPrime;
    }

    void rotate(final Vector3f vec, float angleDeg, final Vector3f origin) {
        // This function ignores Z values
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float xPrime = origin.x + ((x * (float) Math.cos(toRadians(angleDeg))) - (y * (float) Math.sin(toRadians(angleDeg))));
        float yPrime = origin.y + ((x * (float) Math.sin(toRadians(angleDeg))) + (y * (float) Math.cos(toRadians(angleDeg))));

        vec.x = xPrime;
        vec.y = yPrime;
    }

    float mapRange(float val, float inMin, float inMax, float outMin, float outMax) {
        return (val - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    int max(int a, int b) {
        return a > b ? a : b;
    }

    int min(int a, int b) {
        return a < b ? a : b;
    }

    float saturate(float val) {
        return val < 0 ? 0 : val > 1 ? 1 : val;
    }

    int hashString(final String str) {
        int hash = Integer.MAX_VALUE;
        int length = (int) str.length();

        for (int i = 0; i < length; i++) {
            hash ^= str.toCharArray()[i];
            hash *= 16777619;
        }

        return hash;
    }

    // String toString(final Vector4f vec4, int precision)
    // {
    // return std::string("(w: ")
    // + toString(vec4.w, precision) + ",x: "
    // + toString(vec4.x, precision) + ",y: "
    // + toString(vec4.y, precision) + ",z: "
    // + toString(vec4.z, precision) + ")";
    // }

    // String toString(final Vector3f vec3, int precision)
    // {
    // return std::string("(x: ")
    // + toString(vec3.x, precision) + ",y: "
    // + toString(vec3.y, precision) + ",z: "
    // + toString(vec3.z, precision) + ")";
    // }

    // String toString(final Vector2f vec2, int precision)
    // {
    // return std::string("(x: ")
    // + toString(vec2.x, precision) + ",y: "
    // + toString(vec2.y, precision) + ")";
    // }

    // String toString(final Vector4i vec4)
    // {
    // return std::string("(w: ")
    // + std::to_string(vec4.w) + ",x: "
    // + std::to_string(vec4.x) + ",y: "
    // + std::to_string(vec4.y) + ",z: "
    // + std::to_string(vec4.z) + ")";
    // }

    // String toString(final glm::ivec3& vec3)
    // {
    // return std::string("(x: ")
    // + std::to_string(vec3.x) + ",y: "
    // + std::to_string(vec3.y) + ",z: "
    // + std::to_string(vec3.z) + ")";
    // }

    // String toString(final glm::ivec2& vec2)
    // {
    // return std::string("(x: ")
    // + std::to_string(vec2.x) + ",y: "
    // + std::to_string(vec2.y) + ")";
    // }

    // String toString(float value, int precision)
    // {
    // final String str = String.valueOf(value);
    // final int precisionIndex = (int) str.indexOf(".") + precision + 1;
    // return str.substring(0, precisionIndex);
    // }

    int length2(final Vector2i vec) {
        return vec.x * vec.x + vec.y * vec.y;
    }

    int length2(final Vector3i vec) {
        return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
    }

    int length2(final Vector4i vec) {
        return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z + vec.w * vec.w;
    }

    int negativeMod(int value, int lowerBound, int upperBound) {
        int rangeSize = upperBound - lowerBound + 1;

        if (value < lowerBound) {
            value += rangeSize * ((lowerBound - value) / rangeSize + 1);
        }

        return lowerBound + (value - lowerBound) % rangeSize;
    }
}
