package Level.Block;

import Game.Async;
import Game.Texture;
import Game.View;
import Level.Level;

import java.util.HashMap;

public abstract class NonStaticAbstractBlock extends AbstractBlock {
    NonStaticAbstractBlock() {
        this.isStatic = false;
    }

    public abstract void init(Level level, View view, HashMap<String, Async<Texture>> textures, long timestamp, int chunkIndex, int xPos, int yPos);
}
