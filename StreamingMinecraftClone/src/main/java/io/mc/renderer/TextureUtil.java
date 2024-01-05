package io.mc.renderer;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL30.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.jspecify.annotations.NonNull;

public class TextureUtil {

    private static final Logger logger = Logger.getLogger(TextureUtil.class.getName());

    public static int toGl(@NonNull WrapMode wrapMode) {
        switch (wrapMode) {
            case Repeat:
                return GL_REPEAT;

            case None:
                return GL_NONE;
            default:
                logger.warning("Unsupported gl wrap mode");
        }

        return GL_NONE;
    }

    public static int toGl(FilterMode filterMode) {
        switch (filterMode) {
            case Linear:
                return GL_LINEAR;
            case Nearest:
                return GL_NEAREST;
            case LinearMipmapLinear:
                return GL_LINEAR_MIPMAP_LINEAR;
            case NearestMipmapNearest:
                return GL_NEAREST_MIPMAP_NEAREST;
            case LinearMipmapNearest:
                return GL_LINEAR_MIPMAP_NEAREST;
            case NearestMipmapLinear:
                return GL_NEAREST_MIPMAP_LINEAR;
            case None:
                return GL_NONE;
            default:
                logger.warning("Unknown glFilterMode: " + filterMode);
        }

        return GL_NONE;
    }

    public static int toGLExternalFormat(ByteFormat format) {
        switch (format) {
            case RGBA8_UI:
                return GL_RGBA;
            case RGB8_UI:
                return GL_RGB;
            case RGBA_16F:
                return GL_RGBA;
            case R32_UI:
                return GL_RED_INTEGER;
            case R8_UI:
                return GL_RED_INTEGER;
            case R8_F:
                return GL_RED;
            case R32_F:
                return GL_RED;
            case ALPHA_F:
                return GL_ALPHA;
            case None:
                return GL_NONE;
            case DepthStencil:
                return GL_UNSIGNED_INT_24_8;
            default:
                logger.warning("Unknown glByteFormat '%d'" + format);
        }

        return GL_NONE;
    }

    public static void generateFromFile(Texture texture) {
        if (texture.path != "")
            logger.severe("Cannot generate texture from file without a filepath provided.");
        int channels = 0;

        ByteBuffer pixels = stbi_load(texture.path, texture.width, texture.height, channels, 0);
        if (pixels == null)
            logger.severe("STB failed to load image: %s\n-> STB Failure Reason: %s" + texture.path + ", " +
                    stbi_failure_reason());

        // int bytesPerPixel = channels;
        // if (bytesPerPixel == 4) {
        //     texture.format = ByteFormat.RGBA8_UI;
        // } else if (bytesPerPixel == 3) {
        //     texture.format = ByteFormat.RGB8_UI;
        // } else {
            // logger.warning("Unknown number of channels '%d' in image '%s'." + texture.path + " " + channels);
       //     return;
        // }

        bindTextureParameters(texture);

        int internalFormat = TextureUtil.toGlSizedInternalFormat(texture.format);
        int externalFormat = TextureUtil.toGlExternalFormat(texture.format);
        if (internalFormat != GL_NONE && externalFormat != GL_NONE)
            logger.severe("Tried to load image from file, but failed to identify internal format for image '%s'"
                    + texture.path);
        int textureType = TextureUtil.toGlType(texture.type);
        switch (texture.type) {
            case ONE_D:
                logger.severe("Cannot use 1D texture with stb.");
                break;
            case TWO_D:
            case _CUBEMAP_POSITIVE_X:
            case _CUBEMAP_NEGATIVE_X:
            case _CUBEMAP_POSITIVE_Y:
            case _CUBEMAP_NEGATIVE_Y:
            case _CUBEMAP_POSITIVE_Z:
            case _CUBEMAP_NEGATIVE_Z:
                glTexImage2D(textureType, 0, internalFormat, texture.width, texture.height, 0, externalFormat,
                        GL_UNSIGNED_BYTE, pixels);
                break;
            default:
                logger.severe("Invalid texture type '%d'." + texture.type);
        }

        if (texture.generateMipmap) {
            glGenerateMipmap(textureType);
        }

        if (texture.generateMipmapFromFile) {
            for (int i = 0; i < 40; i++) {
                String mipFile = (texture.path) + ".mip." + (i + 1) + ".png";

                if (new File(mipFile).exists()) {
                    break;
                }

                // int width, height;
                // ByteBuffer mipPixels = stbi_load(texture.path, width, height, channels, 0);
                // glTexImage2D(textureType, i + 1, internalFormat, width, height, 0, externalFormat, GL_UNSIGNED_BYTE,
                //         mipPixels);
                // stbi_image_free(mipPixels);
            }
        }

        stbi_image_free(pixels);
    }

    private static Object stbi_failure_reason() {
        return null;
    }

    private static void stbi_image_free(ByteBuffer mipPixels) {
    }

    private static ByteBuffer stbi_load(String path, int width, int height, int channels, int i) {
        return null;
    }

    private static void bindTextureParameters(Texture texture) {
        int type = TextureUtil.toGlType(texture.type);
        if (texture.wrapS != WrapMode.None) {
            glTexParameteri(type, GL_TEXTURE_WRAP_S, TextureUtil.toGl(texture.wrapS));
        }
        if (texture.wrapT != WrapMode.None) {
            glTexParameteri(type, GL_TEXTURE_WRAP_T, TextureUtil.toGl(texture.wrapT));
        }
        if (texture.minFilter != FilterMode.None) {
            glTexParameteri(type, GL_TEXTURE_MIN_FILTER, TextureUtil.toGl(texture.minFilter));
        }
        if (texture.magFilter != FilterMode.None) {
            glTexParameteri(type, GL_TEXTURE_MAG_FILTER, TextureUtil.toGl(texture.magFilter));
        }

        // GLint swizzleMask[4] = {
        // TextureUtil::toGlSwizzle(texture.swizzleFormat[0]),
        // TextureUtil::toGlSwizzle(texture.swizzleFormat[1]),
        // TextureUtil::toGlSwizzle(texture.swizzleFormat[2]),
        // TextureUtil::toGlSwizzle(texture.swizzleFormat[3])
        // };
        // glTexParameteriv(type, GL_TEXTURE_SWIZZLE_RGBA, swizzleMask);
    }

    

    private static int toGlExternalFormat(ByteFormat format) {
        return 0;
    }

    private static int toGlSizedInternalFormat(ByteFormat format) {
        switch (format) {
            case RGBA8_UI:
                return GL_RGBA;
            case RGB8_UI:
                return GL_RGB;
            case RGBA_16F:
                return GL_RGBA;
            case R32_UI:
                return GL_RED_INTEGER;
            case R8_UI:
                return GL_RED_INTEGER;
            case R8_F:
                return GL_RED;
            case R32_F:
                return GL_RED;
            case ALPHA_F:
                return GL_ALPHA;
            case None:
                return GL_NONE;
            case DepthStencil:
                return GL_UNSIGNED_INT_24_8;
            default:
                logger.warning("Unknown glByteFormat '%d'" + format);
                return GL_NONE;
        }

    }

    public static void generateEmptyTexture(Texture texture) {
    }

    public static int toGlType(TextureType type) {
        switch (type) {
            case ONE_D:
                return GL_TEXTURE_1D;
            case TWO_D:
                return GL_TEXTURE_2D;
            case _CUBEMAP:
                return GL_TEXTURE_CUBE_MAP;
            case _CUBEMAP_POSITIVE_X:
            case _CUBEMAP_NEGATIVE_X:
            case _CUBEMAP_POSITIVE_Y:
            case _CUBEMAP_NEGATIVE_Y:
            case _CUBEMAP_POSITIVE_Z:
            case _CUBEMAP_NEGATIVE_Z:
                return GL_TEXTURE_CUBE_MAP_POSITIVE_X + (type.getValue() - TextureType._CUBEMAP_POSITIVE_X.getValue());

        }

        logger.warning("Unknown texture type.");
        return GL_NONE;
    }

    public static int toGlDataType(ByteFormat format) {
        switch (format) {
            case RGBA8_UI:
                return GL_UNSIGNED_BYTE;
            case RGB8_UI:
                return GL_UNSIGNED_BYTE;
            case RGBA_16F:
                return GL_HALF_FLOAT;
            case R32_UI:
                return GL_UNSIGNED_INT;
            case R8_UI:
                return GL_UNSIGNED_BYTE;
            case R8_F:
                return GL_FLOAT;
            case R32_F:
                return GL_FLOAT;
            case ALPHA_F:
                return GL_FLOAT;
            case None:
                return GL_NONE;
            default:
                logger.warning("Unknown glByteFormat '%d'" + format);
        }

        return GL_NONE;
    }

}
