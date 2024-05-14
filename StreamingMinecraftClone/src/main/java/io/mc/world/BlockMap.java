package io.mc.world;

import java.util.ArrayList;
import java.util.List;

import io.mc.renderer.Sprite;
import io.mc.renderer.Texture;

public class BlockMap {

    public static final short NULL_BLOCK_ID = 0;

    public static Sprite sprite = null;
    public static Texture texture = null;

    public static final Block NULL_BLOCK = new Block(NULL_BLOCK_ID, (short) 0, (short) 0, (short) 0);
    public static final Block AIR_BLOCK = new Block((short) 1, (short) 0, (short) 0, (short) 0);

    static List<Short> blockFormats = new ArrayList<>();
    public static BlockFormat getBlock(short blockId) {
        throw new UnsupportedOperationException();
    }

    TextureFormat getTextureFormat(final String textureName) {
        // TODO document why this method is empty
        throw new UnsupportedOperationException();
    }

    BlockFormat getBlock(final String name) {
        throw new UnsupportedOperationException();
    }

    int getBlockId(final String name) {
        throw new UnsupportedOperationException();
    }

    public BlockFormat getBlock(int blockId) {
        throw new UnsupportedOperationException();
    }

    public static void loadBlocks(String textureFormatConfig, final String itemFormatConfig, String blockFormatConfig) {
        throw new UnsupportedOperationException();
    }

    void loadBlockItemTextures(String blockFormatConfig) {
        throw new UnsupportedOperationException();
    }

    void loadCraftingRecipes(String craftingRecipesConfig) {
        throw new UnsupportedOperationException();
    }

    public static void uploadTextureCoordinateMapToGpu() {
        throw new UnsupportedOperationException();
    }

    void patchTextureMaps(final Texture blockTexture, final Texture itemTexture) {
        throw new UnsupportedOperationException();
    }

    void patchBlockItemTextureMaps(final Texture blockItemTexture) {
        throw new UnsupportedOperationException();
    }

    void generateBlockItemPictures(String blockFormatConfig, String outputPath) {
        throw new UnsupportedOperationException();
    }

    int getTextureCoordinatesTextureId() {
        throw new UnsupportedOperationException();
    }

    final List<CraftingRecipe> getAllCraftingRecipes() {
        throw new UnsupportedOperationException();
    }

}
