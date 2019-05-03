package Level;

import Game.*;
import Level.Block.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Chunk {
    private LinkedList<Entity> entities = new LinkedList<>();
    public final int chunkIndex;
    public final double xTranslation;
    boolean currentlyLoaded = false;
    boolean currentlyPaused = true;

    public ArrayList<ArrayList<AbstractBlock>> blockList = new ArrayList<>(81);
    public ArrayList<Async<Integer>> spriteIDs = new ArrayList<Async<Integer>>(81) {
        {
            for (int i = 0; i < 81; i++) {
                this.add(null);
            }
        }
    };

    public Chunk(int chunkIndex) {
        this.xTranslation = chunkIndex * 9;
        this.chunkIndex = chunkIndex;
        for (int y = 0; y < 9; y++) {
            this.blockList.add(new ArrayList<AbstractBlock>(9));
            for (int x = 0; x < 9; x++) {
                this.blockList.get(y).add(null);
            }
        }
    }

    public void loadStringList(String[] names) {
        if (names.length != 81) {
            System.err.println("names.length should equal 81, instead is equals: ".concat(Integer.toString(names.length)));
            return;
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                try {
                    String name = names[x + y * 9];
                    if (name != null) {
                        this.blockList.get(y).set(x, (AbstractBlock) (BlockList.getClassForName(name).getDeclaredConstructor().newInstance()));
                    } else {
                        this.blockList.get(y).set(x, null);
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadChunk(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp) {
        this.currentlyLoaded = true;
        this.currentlyPaused = false;
        for (int y = 0; y < 9; y++) {
            ArrayList<AbstractBlock> row = this.blockList.get(y);
            for (int x = 0; x < 9; x++) {
                AbstractBlock block = row.get(x);
                if (block != null) {
                    if (block.isStatic) {
                        String texturePath = ((StaticAbstractBlock) block).texturePath;
                        if (!textures.containsKey(texturePath)) {
                            textures.put(texturePath, view.loadTexture(texturePath));
                        }
                        this.spriteIDs.set(x + y * 9, view.createTexturedRectangle(x, x + 1, y + 1, y, Gameloop.WORLD_LAYER, textures.get(texturePath),
                                Vector3f.EMPTY, Vector3f.EMPTY, timestamp));
                    } else {
                        ((NonStaticAbstractBlock) block).init(level, view, textures, timestamp, this.chunkIndex, x, y);
                    }
                }
            }
        }
    }

    public void deleteChunk(Level level, View view) {
        this.entities.forEach(entity -> entity.pause(view));
        this.entities.clear();
        this.spriteIDs.stream().filter(Objects::nonNull).forEach(view::deleteDrawable);
        level.chunks.set(this.chunkIndex, null);
    }

    public void translateChunk(View view, long timestamp, Vector3f translation, Vector3f velocity) {
        view.updatePositions((ArrayList<Async<Integer>>) (this.spriteIDs.stream().filter(Objects::nonNull).collect(Collectors.toList())), translation, velocity, timestamp);
        this.entities.forEach(entity -> entity.updateTranslation(translation.values[0], velocity.values[0], view, timestamp));
    }

    public void moveEntities(ArrayList<Chunk> chunks, Gameloop gameloop, long timestamp, Level lvl) {
        boolean entCol;
        this.entities.forEach(entity -> entity.doMove(chunks, gameloop, timestamp));

        for (Entity entity : entities) {
            for (Entity target : entities) {
                if (entity != target) {
                    if (entity.collisionEntEnt(target)) {
                        //break;
                    }

                }
            }
            entity.collisionEntBlc(lvl.getBlockListAtIndex(this.chunkIndex-1), -1);
            entity.collisionEntBlc(blockList, 0);
            entity.collisionEntBlc(lvl.getBlockListAtIndex(this.chunkIndex+1), 1);
        }

        this.entities.forEach(entity -> entity.updatePos());
    }

    public void updateEntitiesChunk(ArrayList<Chunk> chunks, View view) {
        int maxchunk = chunks.size() - 1;
        LinkedList<Entity> entityLinkedList = new LinkedList(this.entities);
        entityLinkedList.forEach(entity -> {
            if (entity.xPos > 9 && this.chunkIndex < maxchunk) {
                    entity.moveToChunk(chunks, this.chunkIndex+1, view);
            } else if (entity.xPos < 0 && this.chunkIndex > 0) {
                entity.moveToChunk(chunks, this.chunkIndex-1, view);
            }
        });
    }

    public void addEntity(Entity entity, View view) {
        entity.unPause(view);
        this.entities.add(entity);
    }

    public void removeEntity(Entity entity, View view) {
        entity.pause(view);
        this.entities.remove(entity);
    }

    public ArrayList<Async<Renderer.Drawable>> pausedStaticBlocks = null;

    public void pause(View view) {
        if (!this.currentlyPaused) {
            this.currentlyPaused = true;
            this.entities.forEach(entity -> entity.pause(view));
            this.pausedStaticBlocks = new ArrayList<>();
            this.spriteIDs.forEach(block -> {
                if (block == null) {
                    this.pausedStaticBlocks.add(null);
                } else {
                    this.pausedStaticBlocks.add(view.getDrawableByID(block));
                }
            });
        }
    }

    public void unPause(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp) {
        if (this.currentlyPaused) {
            this.currentlyPaused = false;
            if (!this.currentlyLoaded) {
                this.loadChunk(level, view, textures, timestamp);
            } else {
                this.entities.forEach(entity -> entity.unPause(view));
                this.spriteIDs = new ArrayList<>();
                this.pausedStaticBlocks.forEach(block -> {
                    if (block == null) {
                        this.spriteIDs.add(null);
                    } else {
                        this.spriteIDs.add(view.addToStage(block));
                    }
                });
            }
        }
    }
}
