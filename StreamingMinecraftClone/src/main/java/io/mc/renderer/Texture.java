package io.mc.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;

import java.nio.IntBuffer;
import java.util.List;
import java.util.logging.Logger;

import org.jspecify.annotations.NonNull;
import org.lwjgl.opengl.GL11;

public class Texture {
    private static final Logger logger = Logger.getLogger(Texture.class.getName());
    private static final int NULL_TEXTURE_ID = Integer.MAX_VALUE;

    public int graphicsId = NULL_TEXTURE_ID;
    public int width = 0;
    public int height = 0;
    public ByteFormat format = ByteFormat.None;
    public String path = "";
    public TextureType type = TextureType.None;
    public FilterMode magFilter;
    public WrapMode wrapS;
    public WrapMode wrapT;
    public FilterMode minFilter;
    ColorChannel[] swizzleFormat = new ColorChannel[4];
    public boolean generateMipmap = false;

    public boolean generateMipmapFromFile = false;

    public Texture() {
        
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.graphicsId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        if (this.graphicsId != NULL_TEXTURE_ID) {
            glDeleteTextures(graphicsId);
        }
    }

    public void uploadSubImage(int offsetX, int offsetY, int width, int height, IntBuffer buffer) {
        if (format == ByteFormat.None) {
            logger.warning("Cannot generate texture without color format.");
        }

        int externalFormat = TextureUtil.toGLExternalFormat(format);

        int dataType = TextureUtil.toGlDataType(format);

        switch (type) {
            case ONE_D:
                // GL11.glTextSubImage1D()
                break;
            case TWO_D:
                break;
            default:
                logger.severe("Invalid texture type " + type);
                break;

        }
    }

    public boolean isNull() {
        return graphicsId == NULL_TEXTURE_ID;
    }

    public static class TextureBuilder {

        private static final Logger logger = Logger.getLogger(TextureBuilder.class.getName());

        private Texture texture;

        public TextureBuilder() {
            this.texture = new Texture();

            texture.type = TextureType.TWO_D;
            texture.minFilter = FilterMode.Linear;
            texture.magFilter = FilterMode.Linear;
            texture.wrapS = WrapMode.None;
            texture.wrapT = WrapMode.None;
            texture.graphicsId = NULL_TEXTURE_ID;
            texture.width = 0;
            texture.height = 0;
            texture.format = ByteFormat.None;
            texture.path = "";
            texture.swizzleFormat[0] = ColorChannel.Red;
            texture.swizzleFormat[1] = ColorChannel.Green;
            texture.swizzleFormat[2] = ColorChannel.Blue;
            texture.swizzleFormat[3] = ColorChannel.Alpha;
            texture.generateMipmap = false;
        }

        public TextureBuilder(@NonNull final Texture texture) {
            this.texture = texture;
        }

        public TextureBuilder setMagFilter(@NonNull FilterMode mode) {
            texture.magFilter = mode;
            return null;
        }

        TextureBuilder setMinFilter(FilterMode mode) {
            texture.minFilter = mode;
            return this;
        }

        TextureBuilder setWrapS(WrapMode mode) {
            texture.wrapS = mode;
            return this;
        }

        TextureBuilder setWrapT(WrapMode mode) {
            texture.wrapT = mode;
            return this;
        }

        TextureBuilder setFormat(ByteFormat format) {
            texture.format = format;
            return this;
        }

        TextureBuilder setFilepath(@NonNull final String filepath) {
            texture.path = filepath;
            return this;

        }

        TextureBuilder setWidth(int width) {
            texture.width = width;
            return this;
        }

        TextureBuilder setHeight(int height) {
            texture.height = height;
            return this;
        }

        TextureBuilder setSwizzle(List<ColorChannel> swizzleMask) {
            if (swizzleMask.size() != 4) {
                logger.warning("Must set swizzle mask to {R G B A} format. Size must be 4");
            } else {
                for (int i = 0; i < 4; i++) {
                    texture.swizzleFormat[i] = swizzleMask.get(i);
                }
            }
            return this;
        }

        TextureBuilder setTextureType(TextureType type) {
            texture.type = type;
            return this;
        }

        TextureBuilder generateTextureObject() {
            texture.graphicsId = glGenTextures();
            return this;
        }

        TextureBuilder setTextureObject(int textureObjectId) {
            texture.graphicsId = textureObjectId;
            return this;
        }

        TextureBuilder generateMipmap() {
            texture.generateMipmap = true;
            return this;

        }

        TextureBuilder generateMipmapFromFile() {
            texture.generateMipmapFromFile = true;
            return this;
        }

        TextureBuilder bindTextureObject() {
            glBindTexture(TextureUtil.toGlType(texture.type), texture.graphicsId);
            return this;

        }

        Texture generate() {
            return generate(false);

        }

        Texture generate(boolean generateFromFilepath) {
            if (generateFromFilepath) {
                TextureUtil.generateFromFile(texture);
            } else {
                TextureUtil.generateEmptyTexture(texture);
            }

            return texture;
        }

        Texture build() {
            return texture;
        }

    }


}
