package Level;

import Game.Gameloop;

public abstract class Entity {
    public abstract void doMove(Gameloop gameloop, long tickStart);
}
