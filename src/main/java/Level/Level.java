package Level;

import Game.Async;
import Game.Texture;
import Game.Vector3f;
import Game.View;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Level {
    final ArrayList<Chunk> chunks;
    final ArrayList<Boolean> isChunkLoaded;
    final ArrayList<Entity> entities = new ArrayList<Entity>();
    final ArrayList<LevelBackground> backgrounds;
    final ThreadLocal<HashMap<String, Async<Texture>>> textures = ThreadLocal.withInitial(HashMap::new);

    public Level(ArrayList<Chunk> chunks, ArrayList<LevelBackground> backgrounds) {
        this.chunks = chunks;
        this.backgrounds = backgrounds;
        this.isChunkLoaded = new ArrayList<>(this.chunks.size());
        for (int i = 0; i < this.chunks.size(); i++) {
            this.isChunkLoaded.add(false);
        }
    }

    public void loadLevel(View view) {
        for (LevelBackground b : backgrounds) {
            if (!textures.get().containsKey(b.imagePath)) {
                textures.get().put(b.imagePath, view.loadTexture(b.imagePath));
            }
            b.spriteID = view.createBackground(b.z_index, textures.get().get(b.imagePath), Vector3f.EMPTY, Vector3f.EMPTY, System.nanoTime(), b.aspectRatio);
        }
    }

    public void unloadLevel(View view) {
        backgrounds.forEach((background) -> view.deleteDrawable(background.spriteID));
        textures.get().forEach((key, value) -> view.unloadTexture(value));
    }

    public static class LevelBackground {
        final String imagePath;
        final float aspectRatio;
        final float z_index;
        Async<Integer> spriteID;
        public LevelBackground(String imagePath, float aspectRatio, float z_index) {
            this.imagePath = imagePath;
            this.aspectRatio = aspectRatio;
            this.z_index = z_index;
        }
    }
}
