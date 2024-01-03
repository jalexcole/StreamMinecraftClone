package io.mc.renderer;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Shader {

    public int programId = 0;
    public int startIndex = 0;
    String filepath = "";

    public Shader() {

    }


    public void destroy() {
    }

    public void bind() {
    }

    public void unbind() {

    }
    
    public void uploadVec4(String varName, final Vector4f vec4) {
        
    }



    public void uploadInt(String string, int i) {
    }

    public void uploadMat4(String uView, Matrix4f viewMatrix) {
    }

    public void compile(String shaderFilepath) {
    }
}
