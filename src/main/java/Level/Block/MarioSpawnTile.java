package Level.Block;

import Game.Async;
import Game.Mario;
import Game.Texture;
import Game.View;
import Level.Level;

import java.util.HashMap;

public class MarioSpawnTile extends NonStaticAbstractBlock {
    private boolean alreadyUsed = false;
    public void init(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp) {
        if (!alreadyUsed) {
            //level.setPlayer(new Mario(view, textures, timestamp));
            alreadyUsed = true;
        }
    }
}
