package Level;

import Game.*;
import Level.Block.AbstractBlock;
import Level.Block.Ground;
import Level.Block.StaticAbstractBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Chunk {
    public ArrayList<ArrayList<AbstractBlock>> blockList = new ArrayList<>(9);
    public Chunk() {
        for (int y = 0; y < 9; y++) {
            this.blockList.add(new ArrayList<AbstractBlock>(9));
            for (int x = 0; x < 9; x ++) {
                this.blockList.get(y).add(null);
            }
        }
    }
    public static Chunk groundChunk() {
        Chunk cnk = new Chunk();
        for (int x = 0; x < 9; x++) {
            cnk.blockList.get(0).set(x, new Ground());
        }
        return cnk;
    }
    public void load(View view, HashMap<String, Async<Texture>> textures, long timestamp) {
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
                        view.createTexturedRectangle(x, x+1, y+1, y, Gameloop.WORLD_LAYER, textures.get(texturePath),
                                Vector3f.EMPTY, Vector3f.EMPTY, timestamp);
                    }
                }
            }
        }
    }
}
