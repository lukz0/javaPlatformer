package Level.Block;

import Game.Async;
import Game.Texture;
import Game.View;
import Level.Level;
import Level.Coin;

import java.util.HashMap;

public class CoinSpawnTile extends NonStaticAbstractBlock {
    private boolean alreadyUsed = false;
    public void init(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp, int chunkIndex, int xPos, int yPos) {
        if (!alreadyUsed) {
            alreadyUsed = true;
            level.addEntityToChunk(chunkIndex, new Coin(view, textures, timestamp, xPos, yPos, chunkIndex, level), view);
        }
    }
}
