package io.mc.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import dev.dominion.ecs.api.Dominion;
import io.mc.core.Application;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class Renderer {

    private static List<Object> batches2d;

    private static Shader shader2D;
    private static Shader line3DShader;
    private static Shader regular3DShader;
    private static Shader batch3DVoxelsShader;
    private static Dominion registry;
    static Camera camera;
    static Frustum cameraFrustum;
    private static Batch<RenderVertex2D>[] batches2D;
    private static Batch batch3DVoxels;
    private static Batch<Object> batch3DRegular;
    private Batch<Object> batch3DLines;

    public static void init(Dominion registry) {

    }

    public static void render() {
    }

    public static void reloadShaders() {
        Renderer.shader2D.destroy();
        line3DShader.destroy();
        regular3DShader.destroy();
        batch3DVoxelsShader.destroy();
        shader2D.compile("assets/shaders/DebugShader2D.glsl");
        line3DShader.compile("assets/shaders/DebugShader3D.glsl");
        regular3DShader.compile("assets/shaders/RegularShader3D.glsl");
        batch3DVoxelsShader.compile("assets/shaders/VoxelShader.glsl");
    }

    public static void free() {
    }

    public static void drawLine(Vector3f origin, Vector3f vector3f, Style red) {
    }

    public static void drawBox(Vector3f blockCenter, Vector3f vector3f, Style defaultstyle) {
    }

    private static void flushBatches2D() {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // std::sort(batches2D.begin(), batches2D.end());

        shader2D.bind();
        shader2D.uploadMat4("uProjection", camera.calculateHUDProjectionMatrix());
        shader2D.uploadMat4("uView", camera.calculateHUDViewMatrix());

        for (Batch<RenderVertex2D> batch2D : batches2D) {
            if (batch2D.numVertices <= 0) {
                batch2D.flush();
                continue;
            }

            for (int i = 0; i < batch2D.textureGraphicsIds.size(); i++) {
                if (batch2D.textureGraphicsIds.get(i) != Integer.MAX_VALUE) {
                    //GL30.glBindTextureUnit(i, batch2D.textureGraphicsIds.get(i).intValue());
                }
            }
            // shader2D.uploadIntArray("uFontTextures[0]", 8, Batch.textureIndices().data());
            // shader2D.uploadIntArray("uTextures[0]", 8, Batch.textureIndices().data() + 8);
            // shader2D.uploadInt("uZIndex", batch2D.zIndex);

            batch2D.flush();

            //            DebugStats::numDrawCalls++;
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    void flushBatches3D() {
        if (batch3DLines.numVertices <= 0) {
            return;
        }

        GL11.glDisable(GL11.GL_CULL_FACE);

        line3DShader.bind();
        line3DShader.uploadMat4("uProjection", camera.calculateProjectionMatrix(registry));
        line3DShader.uploadMat4("uView", camera.calculateViewMatrix(registry));
        // line3DShader.uploadFloat("uAspectRatio", Application.getWindow().getAspectRatio());

        batch3DLines.flush();

        GL11.glEnable(GL11.GL_CULL_FACE);

        regular3DShader.bind();
        regular3DShader.uploadMat4("uProjection", camera.calculateProjectionMatrix(registry));
        regular3DShader.uploadMat4("uView", camera.calculateViewMatrix(registry));
        for (int i = 0; i < batch3DRegular.textureGraphicsIds.size(); i++) {
            if (batch3DRegular.textureGraphicsIds.get(i) != Integer.MAX_VALUE) {
                // GL11.glBindTextureUnit(i, batch3DRegular.textureGraphicsIds[i]);
            }
        }
        // regular3DShader.uploadIntArray("uTextures[0]", 16, Batch.textureIndices().data());

        batch3DRegular.flush();

        // DebugStats::numDrawCalls += 2;
    }

    public static void flushVoxelBatches() {
        if (batch3DVoxels.numVertices <= 0) {
            return;
        }

        GL11.glEnable(GL11.GL_CULL_FACE);

        batch3DVoxelsShader.bind();
        batch3DVoxelsShader.uploadMat4("uProjection", camera.calculateProjectionMatrix(registry));
        batch3DVoxelsShader.uploadMat4("uView", camera.calculateViewMatrix(registry));

        batch3DVoxels.flush();

        // DebugStats::numDrawCalls += 1;
    }

    private static void flushBatches3D(final Matrix4f projectionMatrix, final Matrix4f viewMatrix) {
        regular3DShader.bind();
        regular3DShader.uploadMat4("uProjection", projectionMatrix);
        regular3DShader.uploadMat4("uView", viewMatrix);
        for (int i = 0; i < batch3DRegular.textureGraphicsIds.size(); i++) {
            if (batch3DRegular.textureGraphicsIds.get(i) != Integer.MAX_VALUE) {
                // glBindTextureUnit(i, batch3DRegular.textureGraphicsIds.get(i).intValue());
            }
        }
        // regular3DShader.uploadIntArray("uTextures[0]", 16, Batch.textureIndices().data());

        batch3DRegular.flush();
    }

    public static void drawTexture2D(Sprite sprite, Vector2f spritePosition, Vector2f size, Style defaultstyle) {
    }

    public static void drawString(String text, Font defaultFont, Vector2f textPos, float scale, Style guiStyle) {
    }

    public static void drawSquare2D(Vector2f inputBoxPos, Vector2f inputBoxSize, Style guiStyle, int i) {
    }

    public static void drawFilledSquare2D(Vector2f inputBoxPos, Vector2f inputBoxSize, Style guiStyle, int i) {
    }

    public static void drawSquare2D(Vector2f buttonPosition, Vector2f size, Style lineStyle) {
    }

    public static void drawFilledSquare2D(Vector2f buttonPosition, Vector2f buttonSize, Style guiStyle) {
    }

    /**
     * @return the batches2d
     */
    public static List<Object> getBatches2d() {
        return batches2d;
    }

    /**
     * @param batches2d the batches2d to set
     */
    public static void setBatches2d(List<Object> batches2d) {
        Renderer.batches2d = batches2d;
    }

    /**
     * @return the shader2D
     */
    public static Shader getShader2D() {
        return shader2D;
    }

    /**
     * @param shader2d the shader2D to set
     */
    public static void setShader2D(Shader shader2d) {
        shader2D = shader2d;
    }

    /**
     * @return the line3DShader
     */
    public static Shader getLine3DShader() {
        return line3DShader;
    }

    /**
     * @param line3dShader the line3DShader to set
     */
    public static void setLine3DShader(Shader line3dShader) {
        line3DShader = line3dShader;
    }

    /**
     * @return the regular3DShader
     */
    public static Shader getRegular3DShader() {
        return regular3DShader;
    }

    /**
     * @param regular3dShader the regular3DShader to set
     */
    public static void setRegular3DShader(Shader regular3dShader) {
        regular3DShader = regular3dShader;
    }

    /**
     * @return the batch3DVoxelsShader
     */
    public static Shader getBatch3DVoxelsShader() {
        return batch3DVoxelsShader;
    }

    /**
     * @param batch3dVoxelsShader the batch3DVoxelsShader to set
     */
    public static void setBatch3DVoxelsShader(Shader batch3dVoxelsShader) {
        batch3DVoxelsShader = batch3dVoxelsShader;
    }

    /**
     * @return the registry
     */
    public static Dominion getRegistry() {
        return registry;
    }

    /**
     * @param registry the registry to set
     */
    public static void setRegistry(Dominion registry) {
        Renderer.registry = registry;
    }

    /**
     * @return the camera
     */
    public static Camera getCamera() {
        return camera;
    }

    /**
     * @param camera the camera to set
     */
    public static void setCamera(Camera camera) {
        Renderer.camera = camera;
    }

    /**
     * @return the cameraFrustum
     */
    public static Frustum getCameraFrustum() {
        return cameraFrustum;
    }

    /**
     * @param cameraFrustum the cameraFrustum to set
     */
    public static void setCameraFrustum(Frustum cameraFrustum) {
        Renderer.cameraFrustum = cameraFrustum;
    }

    /**
     * @return the batches2D
     */
    public static Batch<RenderVertex2D>[] getBatches2D() {
        return batches2D;
    }

    /**
     * @param batches2d the batches2D to set
     */
    public static void setBatches2D(Batch<RenderVertex2D>[] batches2d) {
        batches2D = batches2d;
    }

    /**
     * @return the batch3DVoxels
     */
    public static Batch getBatch3DVoxels() {
        return batch3DVoxels;
    }

    /**
     * @param batch3dVoxels the batch3DVoxels to set
     */
    public static void setBatch3DVoxels(Batch batch3dVoxels) {
        batch3DVoxels = batch3dVoxels;
    }

    /**
     * @return the batch3DRegular
     */
    public static Batch<Object> getBatch3DRegular() {
        return batch3DRegular;
    }

    /**
     * @param batch3dRegular the batch3DRegular to set
     */
    public static void setBatch3DRegular(Batch<Object> batch3dRegular) {
        batch3DRegular = batch3dRegular;
    }

    /**
     * @return the batch3DLines
     */
    public Batch<Object> getBatch3DLines() {
        return batch3DLines;
    }

    /**
     * @param batch3dLines the batch3DLines to set
     */
    public void setBatch3DLines(Batch<Object> batch3dLines) {
        batch3DLines = batch3dLines;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batch3DLines == null) ? 0 : batch3DLines.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Renderer))
            return false;
        Renderer other = (Renderer) obj;
        if (batch3DLines == null) {
            if (other.batch3DLines != null)
                return false;
        } else if (!batch3DLines.equals(other.batch3DLines))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Renderer [batch3DLines=" + batch3DLines + "]";
    }

    

}
