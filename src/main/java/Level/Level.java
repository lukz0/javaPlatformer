package Level;

import Game.*;
import Level.Block.AbstractBlock;

import java.util.*;

public class Level {
    final ArrayList<Chunk> chunks;
    private HashSet<Chunk> activeChunks = new HashSet<>();
    final ArrayList<Boolean> isChunkLoaded;
    final ArrayList<LevelBackground> backgrounds;
    final ThreadLocal<HashMap<String, Async<Texture>>> textures = ThreadLocal.withInitial(HashMap::new);
    public Mario player = null;

    public Level(ArrayList<LevelBackground> backgrounds, Tilemap tilemap) {
        this.chunks = new ArrayList<Chunk>(tilemap.chunkAmount);
        for (int chunkIndex = 0; chunkIndex < tilemap.chunkAmount; chunkIndex++) {
            this.chunks.add(new Chunk(chunkIndex));
            String[] tiles = {
                    tilemap.tileNames[0 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 0 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 0 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 9 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 9 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 18 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 18 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 27 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 27 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 36 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 36 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 45 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 45 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 54 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 54 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 63 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 63 * tilemap.chunkAmount + chunkIndex * 9],
                    tilemap.tileNames[0 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[1 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[2 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[3 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[4 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[5 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[6 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[7 + 72 * tilemap.chunkAmount + chunkIndex * 9], tilemap.tileNames[8 + 72 * tilemap.chunkAmount + chunkIndex * 9]
            };
            this.chunks.get(chunkIndex).loadStringList(tiles);
        }

        this.backgrounds = backgrounds;
        this.isChunkLoaded = new ArrayList<>(this.chunks.size());
        for (int i = 0; i < this.chunks.size(); i++) {
            this.isChunkLoaded.add(false);
        }
    }

    public Level loadLevel(View view, long timestamp) {
        for (LevelBackground b : backgrounds) {
            if (!textures.get().containsKey(b.imagePath)) {
                textures.get().put(b.imagePath, view.loadTexture(b.imagePath));
            }
            b.spriteID = view.createBackground(b.z_index, textures.get().get(b.imagePath), Vector3f.EMPTY, b.tickTranslation, System.nanoTime(), b.aspectRatio);
            loadChunk(0, view, timestamp);
        }
        this.activeChunks.add(this.chunks.get(0));
        if (this.chunks.size() >= 2) {
            loadChunk(1, view, timestamp);
            this.chunks.get(1).translateChunk(view, timestamp, new Vector3f(9, 0, 0), Vector3f.EMPTY);
            this.activeChunks.add(this.chunks.get(1));
        }
        if (this.chunks.size() >= 3) {
            loadChunk(2, view, timestamp);
            this.chunks.get(2).translateChunk(view, timestamp, new Vector3f(18, 0, 0), Vector3f.EMPTY);
            this.activeChunks.add(this.chunks.get(2));
        }
        return this;
    }

    public void deleteLevel(View view) {
        if (this.isPaused) {
            this.unPause(view, System.currentTimeMillis());
        }
        this.backgrounds.forEach((background) -> view.deleteDrawable(background.spriteID));
        this.chunks.stream().forEach(chunk -> chunk.deleteChunk(this, view));
        this.textures.get().forEach((key, value) -> view.unloadTexture(value));
    }

    private void loadChunk(int i, View view, long timestamp) {
        this.chunks.get(i).loadChunk(this, view, this.textures.get(), timestamp);
    }

    public void doPhysics(Gameloop gameloop, long timestamp, View view) {
        if (this.player != null) {
            this.chunks.stream().filter(cnk -> cnk.currentlyLoaded).filter(cnk -> !cnk.currentlyPaused)
                    .peek(cnk -> cnk.moveEntities(this.chunks, gameloop, timestamp, this))
                    .forEach(chunk -> chunk.updateEntitiesChunk(this.chunks, view));
            this.chunks.stream().filter(cnk -> cnk.currentlyLoaded).filter(cnk -> !cnk.currentlyPaused).forEach(cnk -> cnk.translateChunk(gameloop.view, timestamp, this.player));
            chunkManager(view);
            this.backgrounds.forEach(b -> b.updateTranslationSum(view, (float) (this.player.xPos + this.player.chunkIndex * 9), (float) this.player.xVelocity, timestamp));
        }
    }

    int playerChunkIndex = 0;

    private void chunkManager(View view) {
        if (Objects.nonNull(this.player)) {
            if (this.player.chunkIndex != this.playerChunkIndex) {
                this.playerChunkIndex = this.player.chunkIndex;
                Chunk chunk;
                try {
                    chunk = this.chunks.get(this.playerChunkIndex + 1);
                    if (chunk.currentlyPaused) {
                        chunk.unPause(this, view, this.textures.get(), System.nanoTime());
                    }
                    this.activeChunks.add(chunk);
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    chunk = this.chunks.get(this.playerChunkIndex - 1);
                    if (chunk.currentlyPaused) {
                        chunk.unPause(this, view, this.textures.get(), System.nanoTime());
                    }
                    this.activeChunks.add(chunk);
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    chunk = this.chunks.get(this.playerChunkIndex + 2);
                    if (!chunk.currentlyPaused) {
                        chunk.pause(view);
                    }
                    this.activeChunks.remove(chunk);
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    chunk = this.chunks.get(this.playerChunkIndex - 2);
                    if (chunk.currentlyPaused) {
                        chunk.pause(view);
                    }
                    this.activeChunks.remove(chunk);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
    }

    public void setPlayer(Mario player) {
        this.player = player;
        this.playerChunkIndex = this.player.chunkIndex;
    }

    public void addEntityToChunk(int index, Entity ent, View view) {
        this.chunks.get(index).addEntity(ent, view);
    }

    public static class LevelBackground {
        final String imagePath;
        final float aspectRatio;
        final float z_index;
        Async<Integer> spriteID;
        final float moveTranslation;
        final Vector3f tickTranslation;
        float tickTranslationSum = 0;

        public LevelBackground(String imagePath, float aspectRatio, float z_index, float moveTranslation, Vector3f tickTranslation) {
            this.imagePath = imagePath;
            this.aspectRatio = aspectRatio;
            this.z_index = z_index;
            this.moveTranslation = moveTranslation;
            this.tickTranslation = tickTranslation;
        }

        void updateTranslationSum(View view, float playerTranslation, float playerVelocity, long timestamp) {
            this.tickTranslationSum += this.tickTranslation.values[0];
            view.updatePosition(this.spriteID, new Vector3f(this.tickTranslationSum + playerTranslation * this.moveTranslation, 0, 0), new Vector3f(this.tickTranslation.values[0] + playerVelocity * this.moveTranslation, 0, 0), timestamp);
        }
    }

    private boolean isPaused = false;

    public void pause(View view) {
        if (!this.isPaused) {
            this.isPaused = true;
            this.activeChunks.forEach(chunk -> chunk.pause(view));
        }
    }

    public void unPause(View view, long timestamp) {
        if (this.isPaused) {
            this.isPaused = false;
            this.activeChunks.forEach(chunk -> chunk.unPause(this, view, this.textures.get(), timestamp));
        }
    }

    private static final ArrayList<ArrayList<AbstractBlock>> EMPTY_BLOCK_LIST = new ArrayList<ArrayList<AbstractBlock>>(9) {
        {
            for (int row = 0; row < 9; row++) {
                super.add(new ArrayList<AbstractBlock>(9) {
                    {
                        for (int column = 0; column < 9; column++) {
                            super.add(null);
                        }
                    }
                });
            }
        }
    };

    ArrayList<ArrayList<AbstractBlock>> getBlockListAtIndex(int i) {
        Chunk chunk;
        try {
            chunk = this.chunks.get(i);
            if (!chunk.currentlyPaused && chunk.currentlyLoaded) {
                return chunk.blockList;
            } else {
                throw new IndexOutOfBoundsException();
            }
        } catch (IndexOutOfBoundsException e) {
            return this.EMPTY_BLOCK_LIST;
        }
    }

    private static final LinkedList<Entity> EMPTY_ENTITY_LIST = new LinkedList<>();

    LinkedList<Entity> getEntityListAtIndex(int i) {
        Chunk chunk;
        try {
            chunk = this.chunks.get(i);
            if (!chunk.currentlyPaused && chunk.currentlyLoaded) {
                return chunk.entities;
            } else {
                throw new IndexOutOfBoundsException();
            }
        } catch (IndexOutOfBoundsException e) {
            return this.EMPTY_ENTITY_LIST;
        }
    }
}
