package Level.Block;

import Game.Gameloop;

public abstract class NonStaticAbstractBlock extends AbstractBlock {
    NonStaticAbstractBlock() {
        this.isStatic = false;
    }
    abstract void init(Gameloop gameloop);
}
