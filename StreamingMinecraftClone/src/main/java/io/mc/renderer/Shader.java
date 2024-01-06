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


    /**
     * @return the programId
     */
    public int getProgramId() {
        return programId;
    }


    /**
     * @param programId the programId to set
     */
    public void setProgramId(int programId) {
        this.programId = programId;
    }


    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }


    /**
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }


    /**
     * @return the filepath
     */
    public String getFilepath() {
        return filepath;
    }


    /**
     * @param filepath the filepath to set
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + programId;
        result = prime * result + startIndex;
        result = prime * result + ((filepath == null) ? 0 : filepath.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Shader))
            return false;
        Shader other = (Shader) obj;
        if (programId != other.programId)
            return false;
        if (startIndex != other.startIndex)
            return false;
        if (filepath == null) {
            if (other.filepath != null)
                return false;
        } else if (!filepath.equals(other.filepath))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "Shader [programId=" + programId + ", startIndex=" + startIndex + ", filepath=" + filepath + "]";
    }

    
}
