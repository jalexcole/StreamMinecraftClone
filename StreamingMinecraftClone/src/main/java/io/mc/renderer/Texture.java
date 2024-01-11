package io.mc.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.IntBuffer;
import java.util.Arrays;
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

    private static void bindTextureParameters(@NonNull final Texture texture) {
        int type = TextureUtil.toGlType(texture.type);

        if (texture.wrapS != WrapMode.None) {
            glTexParameteri(type, GL_TEXTURE_WRAP_S, TextureUtil.toGl(texture.wrapS));
        }
        if (texture.wrapT != WrapMode.None)
		{
			glTexParameteri(type, GL_TEXTURE_WRAP_T, TextureUtil.toGl(texture.wrapT));
		}
		if (texture.minFilter != FilterMode.None)
		{
			glTexParameteri(type, GL11.GL_TEXTURE_MIN_FILTER, TextureUtil.toGl(texture.minFilter));
		}
		if (texture.magFilter != FilterMode.None)
		{
			glTexParameteri(type, GL_TEXTURE_MAG_FILTER, TextureUtil.toGl(texture.magFilter));
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + graphicsId;
        result = prime * result + width;
        result = prime * result + height;
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((magFilter == null) ? 0 : magFilter.hashCode());
        result = prime * result + ((wrapS == null) ? 0 : wrapS.hashCode());
        result = prime * result + ((wrapT == null) ? 0 : wrapT.hashCode());
        result = prime * result + ((minFilter == null) ? 0 : minFilter.hashCode());
        result = prime * result + Arrays.hashCode(swizzleFormat);
        result = prime * result + (generateMipmap ? 1231 : 1237);
        result = prime * result + (generateMipmapFromFile ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Texture))
            return false;
        Texture other = (Texture) obj;
        if (graphicsId != other.graphicsId)
            return false;
        if (width != other.width)
            return false;
        if (height != other.height)
            return false;
        if (format != other.format)
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (type != other.type)
            return false;
        if (magFilter != other.magFilter)
            return false;
        if (wrapS != other.wrapS)
            return false;
        if (wrapT != other.wrapT)
            return false;
        if (minFilter != other.minFilter)
            return false;
        if (!Arrays.equals(swizzleFormat, other.swizzleFormat))
            return false;
        if (generateMipmap != other.generateMipmap)
            return false;
        if (generateMipmapFromFile != other.generateMipmapFromFile)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Texture [graphicsId=" + graphicsId + ", width=" + width + ", height=" + height + ", format=" + format
                + ", path=" + path + ", type=" + type + ", magFilter=" + magFilter + ", wrapS=" + wrapS + ", wrapT="
                + wrapT + ", minFilter=" + minFilter + ", swizzleFormat=" + Arrays.toString(swizzleFormat)
                + ", generateMipmap=" + generateMipmap + ", generateMipmapFromFile=" + generateMipmapFromFile + "]";
    }

    
}
