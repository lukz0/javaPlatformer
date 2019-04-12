package Level;

import Game.Gameloop;

import java.util.ArrayList;

public abstract class Entity {
    public int chunkIndex;
    public double xPos;
    public double yPos;
    public double xVelocity;
    public double yVelocity;
    public double width;
    public double height;
    public abstract void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart);
}
