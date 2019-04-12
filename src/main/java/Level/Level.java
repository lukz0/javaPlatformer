package Level;

import Game.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Level {
    final ArrayList<Chunk> chunks;
    final ArrayList<Boolean> isChunkLoaded;
    //final ArrayList<Entity> entities = new ArrayList<Entity>();
    final ArrayList<LevelBackground> backgrounds;
    final ThreadLocal<HashMap<String, Async<Texture>>> textures = ThreadLocal.withInitial(HashMap::new);
    private Mario player = null;

    public Level(ArrayList<LevelBackground> backgrounds, Tilemap tilemap) {
        this.chunks = new ArrayList<Chunk>(tilemap.chunkAmount);
        for (int chunkIndex = 0; chunkIndex < tilemap.chunkAmount; chunkIndex++) {
            this.chunks.add(new Chunk(chunkIndex));
            String[] tiles = {
                    tilemap.tileNames[0+0*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+0*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+0*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+0*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+0*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+0*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+0*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+0*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+0*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+9*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+9*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+9*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+9*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+9*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+9*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+9*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+9*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+9*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+18*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+18*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+18*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+18*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+18*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+18*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+18*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+18*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+18*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+27*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+27*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+27*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+27*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+27*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+27*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+27*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+27*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+27*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+36*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+36*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+36*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+36*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+36*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+36*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+36*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+36*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+36*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+45*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+45*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+45*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+45*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+45*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+45*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+45*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+45*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+45*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+54*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+54*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+54*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+54*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+54*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+54*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+54*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+54*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+54*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+63*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+63*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+63*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+63*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+63*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+63*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+63*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+63*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+63*tilemap.chunkAmount+chunkIndex*9],
                    tilemap.tileNames[0+72*tilemap.chunkAmount+chunkIndex], tilemap.tileNames[1+72*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[2+72*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[3+72*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[4+72*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[5+72*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[6+72*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[7+72*tilemap.chunkAmount+chunkIndex*9], tilemap.tileNames[8+72*tilemap.chunkAmount+chunkIndex*9]
            };
            this.chunks.get(chunkIndex).loadStringList(tiles);
        }

        this.backgrounds = backgrounds;
        this.isChunkLoaded = new ArrayList<>(this.chunks.size());
        for (int i = 0; i < this.chunks.size(); i++) {
            this.isChunkLoaded.add(false);
        }
    }

    public void loadLevel(View view, long timestamp) {
        for (LevelBackground b : backgrounds) {
            if (!textures.get().containsKey(b.imagePath)) {
                textures.get().put(b.imagePath, view.loadTexture(b.imagePath));
            }
            b.spriteID = view.createBackground(b.z_index, textures.get().get(b.imagePath), Vector3f.EMPTY, b.tickTranslation, System.nanoTime(), b.aspectRatio);
            loadChunk(0, view, timestamp);
            loadChunk(1, view, timestamp);
            this.chunks.get(1).translateChunk(view, timestamp, new Vector3f(9, 0, 0), Vector3f.EMPTY);
        }
    }

    public void unloadLevel(View view) {
        backgrounds.forEach((background) -> view.deleteDrawable(background.spriteID));
        textures.get().forEach((key, value) -> view.unloadTexture(value));
    }

    private void loadChunk(int i, View view, long timestamp) {
        this.chunks.get(i).loadChunk(this, view, this.textures.get(), timestamp);
    }

    public void doPhysics(Gameloop gameloop, long timestamp) {
        this.chunks.forEach(cnk -> cnk.moveEntities(this.chunks, gameloop, timestamp));
        if (this.player != null) {
            this.chunks.forEach(cnk -> cnk.translateChunk(gameloop.view, timestamp, new Vector3f(-(float)this.player.xPos+7.5f+cnk.chunkIndex*9, 0, 0), new Vector3f(-(float)this.player.xVelocity, 0, 0)));
        }
    }

    public void setPlayer(Mario player) {
        this.player = player;
    }

    public void addEntityToChunk(int index, Entity ent) {
        this.chunks.get(index).addEntity(ent);
    }

    public static class LevelBackground {
        final String imagePath;
        final float aspectRatio;
        final float z_index;
        Async<Integer> spriteID;
        final float moveTranslation;
        final Vector3f tickTranslation;
        public LevelBackground(String imagePath, float aspectRatio, float z_index, float moveTranslation, Vector3f tickTranslation) {
            this.imagePath = imagePath;
            this.aspectRatio = aspectRatio;
            this.z_index = z_index;
            this.moveTranslation = moveTranslation;
            this.tickTranslation = tickTranslation;
        }
    }
}
