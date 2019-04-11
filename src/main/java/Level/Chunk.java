package Level;

import Game.*;
import Level.Block.AbstractBlock;
import Level.Block.BlockList;
import Level.Block.Ground;
import Level.Block.StaticAbstractBlock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

public class Chunk {
    public ArrayList<ArrayList<AbstractBlock>> blockList = new ArrayList<>(81);
    public ArrayList<Async<Integer>> spriteIDs = new ArrayList<Async<Integer>>(81) {
        {
            for (int i = 0; i < 81; i++) {
                this.add(null);
            }
        }
    };
    public Chunk() {
        for (int y = 0; y < 9; y++) {
            this.blockList.add(new ArrayList<AbstractBlock>(9));
            for (int x = 0; x < 9; x ++) {
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
                    String name = names[x+y*9];
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
    public void loadChunk(View view, HashMap<String, Async<Texture>> textures, long timestamp) {
        for (int y = 0; y < 9; y++) {
            ArrayList<AbstractBlock> row = this.blockList.get(y);
            for (int x = 0; x < 9; x++) {
                AbstractBlock block = row.get(x);
                if (block != null) {
                    if (block.isStatic) {
                        String texturePath = ((StaticAbstractBlock)block).texturePath;
                        if (!textures.containsKey(texturePath)) {
                            textures.put(texturePath, view.loadTexture(texturePath));
                        }
                        this.spriteIDs.set(x+y*9, view.createTexturedRectangle(x, x+1, y+1, y, Gameloop.WORLD_LAYER, textures.get(texturePath),
                                Vector3f.EMPTY, Vector3f.EMPTY, timestamp));
                    }
                }
            }
        }
    }

    public void translateChunk(View view, long timestamp, Vector3f translation, Vector3f velocity) {
        view.updatePositions((ArrayList<Async<Integer>>)(this.spriteIDs.stream().filter(id ->id!=null).collect(Collectors.toList())), translation, velocity, timestamp);
    }
}
