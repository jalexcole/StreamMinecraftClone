package io.mc.renderer;

import java.util.HashMap;
import java.util.Map;

public class Sprites {

    private static Map<String, SpriteSheet> spritesheets = new HashMap<>();

    private static void load(String filepath) {

    }

    public static final Map<String, Sprites> getSpritesheet(String filepath) {
        var spritesheet = spritesheets.get(filepath);
        if (spritesheet == null) {
            load(filepath);
            
        }
        
        return spritesheet.sprites;

    }

    public static void freeAllSpritesheets() {
        spritesheets.keySet().forEach(key -> spritesheets.get(key).clear());

    }

}

class SpriteSheet {
    Texture texture;
    Map<String, Sprite> sprites = new HashMap<>();
    public Object clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }
}
