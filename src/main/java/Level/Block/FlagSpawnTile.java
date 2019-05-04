package Level.Block;

import Game.*;
import Level.Level;

import java.util.HashMap;

public class FlagSpawnTile extends NonStaticAbstractBlock{
    private boolean alreadyUsed = false;
    public void init(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp, int chunkIndex, int xPos, int yPos) {
        if (!alreadyUsed) {
            alreadyUsed = true;
            level.addEntityToChunk(chunkIndex, new Flag(view, textures, timestamp, xPos, yPos, chunkIndex, level), view);
        }
    }
}
