package io.mc.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

import dev.dominion.ecs.api.Dominion;
import io.mc.core.Application;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.logging.Logger;

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
    private static Batch<RenderVertexLine> batch3DLines = new Batch<>();

    private static final Logger logger = Logger.getLogger(Renderer.class.getName());

    public static void init(@NonNull Dominion sceneRegistry) {
        Renderer.registry = sceneRegistry;
        camera = null;
        batch3DLines.numVertices = 0;

        // Enable render parameters
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        shader2D.compile("assets/shaders/DebugShader2D.glsl");
        line3DShader.compile("assets/shaders/DebugShader3D.glsl");
        regular3DShader.compile("assets/shaders/RegularShader3D.glsl");
        batch3DVoxelsShader.compile("assets/shaders/VoxelShader.glsl");
    }

    public static void render() {
        flushBatches3D();
        flushBatches2D();
        flushVoxelBatches();
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
        for (Batch<RenderVertex2D> batch2D : batches2D) {
            batch2D.free();
        }
        batch3DLines.free();
        batch3DRegular.free();
        batch3DVoxels.free();

        shader2D.destroy();
        line3DShader.destroy();
        regular3DShader.destroy();
        batch3DVoxelsShader.destroy();
    }

    public static void drawLine(Vector3f start, Vector3f end, Style style) {
        if (batch3DLines.numVertices + 6 >= Batch.maxBatchSize) {
            flushBatches3D();
        }

        // First triangle
        RenderVertexLine v = new RenderVertexLine();
        v.isStart = 1.0f;
        v.start = start;
        v.end = end;
        v.direction = -1.0f;
        // FIXME: v.color = style.color;
        v.strokeWidth = style.strokeWidth;
        batch3DLines.addVertex(v);

        v.isStart = 1.0f;
        v.start = start;
        v.end = end;
        v.direction = 1.0f;
        // FIXME: v.color = style.color;
        v.strokeWidth = style.strokeWidth;
        batch3DLines.addVertex(v);

        v.isStart = 0.0f;
        v.start = start;
        v.end = end;
        v.direction = 1.0f;
        // FIXME: v.color = style.color;
        v.strokeWidth = style.strokeWidth;
        batch3DLines.addVertex(v);

        // Second triangle
        v.isStart = 1.0f;
        v.start = start;
        v.end = end;
        v.direction = -1.0f;
        // FIXME: v.color = style.color;
        v.strokeWidth = style.strokeWidth;
        batch3DLines.addVertex(v);

        v.isStart = 0.0f;
        v.start = start;
        v.end = end;
        v.direction = 1.0f;
        // FIXME: v.color = style.color;
        v.strokeWidth = style.strokeWidth;
        batch3DLines.addVertex(v);

        v.isStart = 0.0f;
        v.start = start;
        v.end = end;
        v.direction = -1.0f;
        // FIXME: v.color = style.color;
        v.strokeWidth = style.strokeWidth;
        batch3DLines.addVertex(v);
    }

    public static void drawBox(Vector3f center, Vector3f size, Style style) {
        // TODO: Do this in a better way... Maybe do sphere check before expensive box check
			if (!cameraFrustum.isBoxVisible(center.sub(size.mul(0.5f), center.add(size.mul(0.5f))))) {
				return;
			}

			Vector3f v0 = center.sub(size.mul(0.5f));
			Vector3f v1 = v0.add(new Vector3f(size.x, 0, 0));
			Vector3f v2 = v0.add(new Vector3f(0, 0, size.z));
			Vector3f v3 = v0.add(new Vector3f(size.x, 0, size.z));

			Vector3f v4 = v0.add(new Vector3f(0, size.y, 0));
			Vector3f v5 = v1.add(new Vector3f(0, size.y, 0));
			Vector3f v6 = v2.add(new Vector3f(0, size.y, 0));
			Vector3f v7 = v3.add(new Vector3f(0, size.y, 0));

			drawLine(v0, v1, style);
			drawLine(v0, v2, style);
			drawLine(v2, v3, style);
			drawLine(v1, v3, style);

			drawLine(v4, v5, style);
			drawLine(v4, v6, style);
			drawLine(v5, v7, style);
			drawLine(v6, v7, style);

			drawLine(v0, v4, style);
			drawLine(v1, v5, style);
			drawLine(v2, v6, style);
			drawLine(v3, v7, style);
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
                    // GL30.glBindTextureUnit(i, batch2D.textureGraphicsIds.get(i).intValue());
                }
            }
            // shader2D.uploadIntArray("uFontTextures[0]", 8,
            // Batch.textureIndices().data());
            // shader2D.uploadIntArray("uTextures[0]", 8, Batch.textureIndices().data() +
            // 8);
            // shader2D.uploadInt("uZIndex", batch2D.zIndex);

            batch2D.flush();

            // DebugStats::numDrawCalls++;
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    static void flushBatches3D() {
        if (batch3DLines.numVertices <= 0) {
            return;
        }

        GL11.glDisable(GL11.GL_CULL_FACE);

        line3DShader.bind();
        line3DShader.uploadMat4("uProjection", camera.calculateProjectionMatrix(registry));
        line3DShader.uploadMat4("uView", camera.calculateViewMatrix(registry));
        // line3DShader.uploadFloat("uAspectRatio",
        // Application.getWindow().getAspectRatio());

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
        // regular3DShader.uploadIntArray("uTextures[0]", 16,
        // Batch.textureIndices().data());

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
        // regular3DShader.uploadIntArray("uTextures[0]", 16,
        // Batch.textureIndices().data());

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
    public Batch<RenderVertexLine> getBatch3DLines() {
        return batch3DLines;
    }

    /**
     * @param batch3dLines the batch3DLines to set
     */
    public void setBatch3DLines(Batch<RenderVertexLine> batch3dLines) {
        Renderer.batch3DLines = batch3dLines;
    }

    static void drawTexturedTriangle2D(
			final Vector2f p0,
			final Vector2f p1,
			final Vector2f p2,
			final Vector2f uv0,
			final Vector2f uv1,
			final Vector2f uv2,
			final Texture texture,
			final Style style,
			int zIndex,
			boolean isFont) {
			Batch<RenderVertex2D> batch2D = getBatch2D(zIndex, texture, true, isFont);
			if (batch2D.numVertices + 3 > Batch.maxBatchSize) {
				batch2D = createBatch2D(zIndex, isFont);
			}

			int texSlot = batch2D.getTextureSlot(texture.graphicsId, isFont);

			// One triangle per sector
			RenderVertex2D v = new RenderVertex2D();
			v.position = p0;
			//v.color = style.color;
			v.textureSlot = texSlot;
			v.textureCoords = uv0;
			batch2D.addVertex(v);

			v.position = p1;
			//v.color = style.color;
			v.textureSlot = texSlot;
			v.textureCoords = uv1;
			batch2D.addVertex(v);

			v.position = p2;
			//v.color = style.color;
			v.textureSlot = texSlot;
			v.textureCoords = uv2;
			batch2D.addVertex(v);
		}

    static void drawTexturedTriangle3D(
            Vector4f p0,
            Vector4f p1,
            Vector4f p2,
            Vector2f uv0,
            Vector2f uv1,
            Vector2f uv2,
            Vector3f normal,
            final Texture texture) {
        if (batch3DRegular.numVertices + 3 > Batch.maxBatchSize) {
            logger.warning("Ran out of batch room for 3D stuff!");
            return;
        }

       //  var texSlot = batch3DRegular.getTextureSlot3D(texture.graphicsId);

        // One triangle per sector
        RenderVertex3D v = new RenderVertex3D();
        v.position = new Vector3f(p0.x, p0.y, p0.z);
        // v.textureSlot = texSlot;
        v.textureCoords = uv0;
        v.normal = normal;
        batch3DRegular.addVertex(v);

        v.position = new Vector3f(p1.x, p1.y, p1.z);
        // v.textureSlot = texSlot;
        v.textureCoords = uv1;
        v.normal = normal;
        batch3DRegular.addVertex(v);

        v.position = new Vector3f(p2.x, p2.y, p2.z);
        // v.textureSlot = texSlot;
        v.textureCoords = uv2;
        v.normal = normal;
        batch3DRegular.addVertex(v);
    }

    static Batch<RenderVertex2D> getBatch2D(int zIndex, final Texture texture, boolean useTexture, boolean isFont) {
        for (Batch<RenderVertex2D> batch2D : batches2D) {
            if (batch2D.hasRoom() && batch2D.zIndex == zIndex &&
                    (!useTexture || batch2D.hasTexture(texture.graphicsId) || batch2D.hasTextureRoom(isFont))) {
                return batch2D;
            }
        }

        return createBatch2D(zIndex, isFont);
    }

    static Batch<RenderVertex2D> createBatch2D(int zIndex, boolean isFont)
		{
throw new UnsupportedOperationException();
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
