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
    // TODO public abstract void pause();
    // Called when reloading an previously paused chunk
    // TODO public abstract void unPause();

    public void moveToChunk(ArrayList<Chunk> chunks, int newChunkIndex) {
        chunks.get(this.chunkIndex).removeEntity(this);
        chunks.get(newChunkIndex).addEntity(this);
    }
}
