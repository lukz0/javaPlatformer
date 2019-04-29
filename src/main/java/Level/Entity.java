package Level;

import Game.Async;
import Game.Gameloop;
import Game.Vector3f;
import Game.View;
import Level.Block.AbstractBlock;

import java.lang.reflect.Array;
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
                new Vector3f((float) (this.xPos + xChunkTranslation), (float) this.yPos, 0),
                new Vector3f((float) (this.xVelocity + xChunkVelocity), (float) this.yVelocity, 0),
                tickstart);
    }

    public abstract boolean collisionEntEnt(Entity target);

    public abstract boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target);

    public abstract void updatePos();

    //TODO
    /*public void currentChunkUpdate(ArrayList<Chunk> chunks) {
        for (this.xPos > 9 || this.xPos < 0) {
            if (this.xPos > 9) {
                if (this.chunkIndex+1 < chunks.size()) {

                }
            }
        }
    }*/

    // Called when the entity moves into a chunk that isn't loaded
    // Should be called before the entity is removed so that the entities drawable is not in the renderers stage
    public abstract void pause(View view);
    // Called when reloading an previously paused chunk
    public abstract void unPause(View view);

    public void moveToChunk(ArrayList<Chunk> chunks, int newChunkIndex, View view) {
        chunks.get(this.chunkIndex).removeEntity(this, view);
        chunks.get(newChunkIndex).addEntity(this, view);
    }
}
