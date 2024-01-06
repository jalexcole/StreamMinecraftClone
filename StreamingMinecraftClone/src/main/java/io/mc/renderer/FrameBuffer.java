package io.mc.renderer;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FrameBuffer {
    
    private static final Logger logger = Logger.getLogger(FrameBuffer.class.getName());

    public int fbo;
    public int width;
    public int height;

    //Depth/Stencil attachment (optional)
    int rbo;
    ByteFormat depthStencilFormat;
    boolean includeDepthStencil;
    
    List<Texture> colorAttachments = new ArrayList<>();

    public void regenerate() {
        if (fbo == Integer.MAX_VALUE) {
            logger.severe("Cannot regenerate framebuffer that has with Fbo id != UINT32_MAX.");
        }

        if (includeDepthStencil && (fbo == Integer.MAX_VALUE)) {
            logger.severe("Cannot regenerate framebuffer that has with Fbo id != UINT32_MAX.");
        }
        
        destroy(false);
        internalGenerate(this);
    }
    


    private void destroy(boolean clearColorAttchmentSpecs) {
        glDeleteFramebuffers(fbo);
        fbo = Integer.MAX_VALUE;

        for (int i = 0; i < colorAttachments.size(); i++) {
            Texture texture = colorAttachments.get(i);
            texture.destroy();
        }

        if (clearColorAttchmentSpecs) {
            colorAttachments.clear();
        }

        if (includeDepthStencil) {
            glDeleteFramebuffers(rbo);
            rbo = Integer.MAX_VALUE;
        }
    }

    public final FloatBuffer readAllPixelsRgb8(final int colorAttachment) {
        final Texture texture = colorAttachments.get(colorAttachment);

        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0 + colorAttachment);

        FloatBuffer pixelBuffer = FloatBuffer.allocate(texture.width * texture.height * 4);

        int externalFormat = TextureUtil.toGLExternalFormat(texture.format);
        glReadPixels(0, 0, texture.width, texture.height, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8, pixelBuffer);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        FloatBuffer output = FloatBuffer.allocate(texture.width * texture.height * 4);

        for (int y = texture.height - 1; y >= 0; y--) {
            for (int x = 0; x < texture.width; x++) {
                int pixIndex = (x + (y * texture.width));
                int outIndex = (x + ((texture.height - y - 1) * texture.width));
                
                output.put(pixelBuffer.get(pixIndex));
                
            }
        }

        return output;
    }



    public void bind() {
        if (fbo == Integer.MAX_VALUE) {
            logger.severe("Tried to bind invalid framebuffer");
        }

        glBindBuffer(GL_FRAMEBUFFER, fbo);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void destroy() {
        destroy(false);
    }

    public Texture getColorAttachment(final int index) {

        return colorAttachments.get(index);
    }

    private static void internalGenerate(FrameBuffer frameBuffer) {
        // glGenFramebuffers(1, frameBuffer.fbo);
    }

    public static class FramebufferBuilder {
        private FrameBuffer framebuffer = new FrameBuffer();

        public FramebufferBuilder(int width, int height) {
            framebuffer.fbo = Integer.MAX_VALUE;
            framebuffer.fbo = Integer.MAX_VALUE;

            framebuffer.width = width;
            framebuffer.height = height;
            framebuffer.includeDepthStencil = false;
            framebuffer.depthStencilFormat = ByteFormat.None;
        }

        public FrameBuffer generate() {
            internalGenerate(framebuffer);
            return framebuffer;
        }

        public FramebufferBuilder includeDepthStencilBuffer() {
            framebuffer.depthStencilFormat = ByteFormat.DepthStencil;
            framebuffer.includeDepthStencil = true;
            return this;
        }

        public FramebufferBuilder addColorAttachment(final Texture textureSpecification) {
            framebuffer.colorAttachments.add(textureSpecification);
            return this;
        }

    }

    /**
     * @return the fbo
     */
    public int getFbo() {
        return fbo;
    }



    /**
     * @param fbo the fbo to set
     */
    public void setFbo(int fbo) {
        this.fbo = fbo;
    }



    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }



    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }



    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }



    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }



    /**
     * @return the rbo
     */
    public int getRbo() {
        return rbo;
    }



    /**
     * @param rbo the rbo to set
     */
    public void setRbo(int rbo) {
        this.rbo = rbo;
    }



    /**
     * @return the depthStencilFormat
     */
    public ByteFormat getDepthStencilFormat() {
        return depthStencilFormat;
    }



    /**
     * @param depthStencilFormat the depthStencilFormat to set
     */
    public void setDepthStencilFormat(ByteFormat depthStencilFormat) {
        this.depthStencilFormat = depthStencilFormat;
    }



    /**
     * @return the includeDepthStencil
     */
    public boolean isIncludeDepthStencil() {
        return includeDepthStencil;
    }



    /**
     * @param includeDepthStencil the includeDepthStencil to set
     */
    public void setIncludeDepthStencil(boolean includeDepthStencil) {
        this.includeDepthStencil = includeDepthStencil;
    }



    /**
     * @return the colorAttachments
     */
    public List<Texture> getColorAttachments() {
        return colorAttachments;
    }



    /**
     * @param colorAttachments the colorAttachments to set
     */
    public void setColorAttachments(List<Texture> colorAttachments) {
        this.colorAttachments = colorAttachments;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fbo;
        result = prime * result + width;
        result = prime * result + height;
        result = prime * result + rbo;
        result = prime * result + ((depthStencilFormat == null) ? 0 : depthStencilFormat.hashCode());
        result = prime * result + (includeDepthStencil ? 1231 : 1237);
        result = prime * result + ((colorAttachments == null) ? 0 : colorAttachments.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof FrameBuffer))
            return false;
        FrameBuffer other = (FrameBuffer) obj;
        if (fbo != other.fbo)
            return false;
        if (width != other.width)
            return false;
        if (height != other.height)
            return false;
        if (rbo != other.rbo)
            return false;
        if (depthStencilFormat != other.depthStencilFormat)
            return false;
        if (includeDepthStencil != other.includeDepthStencil)
            return false;
        if (colorAttachments == null) {
            if (other.colorAttachments != null)
                return false;
        } else if (!colorAttachments.equals(other.colorAttachments))
            return false;
        return true;
    }



    @Override
    public String toString() {
        return "FrameBuffer [fbo=" + fbo + ", width=" + width + ", height=" + height + ", rbo=" + rbo
                + ", depthStencilFormat=" + depthStencilFormat + ", includeDepthStencil=" + includeDepthStencil
                + ", colorAttachments=" + colorAttachments + "]";
    }
    
    
    
}
