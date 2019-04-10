package Level;

import Level.Block.AbstractBlock;
import Level.Block.Ground;

import java.util.ArrayList;

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
}
