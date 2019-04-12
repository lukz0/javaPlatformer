package Level.Block;

import Game.*;
import Level.Level;

import java.util.HashMap;

public class MarioSpawnTile extends NonStaticAbstractBlock {
    private boolean alreadyUsed = false;
    public void init(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp, int chunkIndex, int xPos, int yPos) {
        if (!alreadyUsed) {
            Mario mario = new Mario(view, textures, timestamp, xPos, yPos, chunkIndex);
            level.setPlayer(mario);
            level.addEntityToChunk(chunkIndex, mario);
            alreadyUsed = true;
        }
    }
}
