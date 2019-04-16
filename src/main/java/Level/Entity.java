package Level;

import Game.Async;
import Game.Gameloop;
import Game.Vector3f;
import Game.View;

import java.util.ArrayList;

public abstract class Entity {
    public int chunkIndex;
    public double xPos;
    public double yPos;
    public double xVelocity;
    public double yVelocity;
    public double width;
    public double height;
    public Async<Integer> drawableID;
    public abstract void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart);
    public void updateTranslation(double xChunkTranslation, double xChunkVelocity, View view, long tickstart) {
        view.updatePosition(this.drawableID,
                new Vector3f((float) (this.xPos + xChunkTranslation), (float) this.yPos, Gameloop.WORLD_LAYER),
                new Vector3f((float) (this.xVelocity + xChunkVelocity), (float) this.yVelocity, 0),
                tickstart);
    }
}
