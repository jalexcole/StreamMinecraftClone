package io.mc.renderer;

class DrawArraysIndirectCommand {
    int count = 0;
    int instanceCount = 0;
    int firstIndex = 0;
    int baseInstance = 0;


    
    @Override
    public String toString() {
        return "DrawArraysIndirectCommand [count=" + count + ", instanceCount=" + instanceCount + ", firstIndex="
                + firstIndex + ", baseInstance=" + baseInstance + "]";
    }



    
}