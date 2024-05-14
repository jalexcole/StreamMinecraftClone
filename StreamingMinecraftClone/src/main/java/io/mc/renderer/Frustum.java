package io.mc.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Frustum {

    Vector4f[] planes = new Vector4f[6];
    Vector3f[] points = new Vector3f[0];

    public Frustum() {

    }

    public Frustum(Matrix4f m) {

    }

    public void update(Matrix4f m) {
        Matrix4f transposedM = m.transpose();

        planes[Planes.LEFT.getValue()] = null;

    }

    public boolean isBoxVisible(Vector3f minp, Vector3f maxp) {
        for (int i = 0; i < Planes.COUNT.getValue(); i++) {

        }

        // check frustum outside/inside box
        int out;
        out = 0;
        for (int i = 0; i < 8; i++)
            out += ((points[i].x > maxp.x) ? 1 : 0);
        if (out == 8)
            return false;
        out = 0;
        for (int i = 0; i < 8; i++)
            out += ((points[i].x < minp.x) ? 1 : 0);
        if (out == 8)
            return false;
        out = 0;
        for (int i = 0; i < 8; i++)
            out += ((points[i].y > maxp.y) ? 1 : 0);
        if (out == 8)
            return false;
        out = 0;
        for (int i = 0; i < 8; i++)
            out += ((points[i].y < minp.y) ? 1 : 0);
        if (out == 8)
            return false;
        out = 0;
        for (int i = 0; i < 8; i++)
            out += ((points[i].z > maxp.z) ? 1 : 0);
        if (out == 8)
            return false;
        out = 0;
        for (int i = 0; i < 8; i++)
            out += ((points[i].z < minp.z) ? 1 : 0);
        if (out == 8)
            return false;
        return false;
    }

    private class ij2k<i extends Planes, j extends Planes> {

    }

    public boolean isBoxVisible(Vector3f sub) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBoxVisible'");
    }

    
}
