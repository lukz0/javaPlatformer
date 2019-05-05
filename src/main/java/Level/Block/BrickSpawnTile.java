package Level.Block;

import Game.Async;
import Game.Brick;
import Game.Texture;
import Game.View;
import Level.Level;

import java.util.HashMap;

public class BrickSpawnTile extends NonStaticAbstractBlock {
    private boolean alreadyUsed = false;

    public void init(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp, int chunkIndex, int xPos, int yPos) {
        if (!alreadyUsed) {
            alreadyUsed = true;
            level.addEntityToChunk(chunkIndex, new Brick(view, textures, timestamp, xPos, yPos, chunkIndex, level), view);
        }
    }
}