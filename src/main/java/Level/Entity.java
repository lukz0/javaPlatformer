package Level;

import Game.*;
import Level.Block.AbstractBlock;
import Level.Block.NonStaticAbstractBlock;
import java.util.ArrayList;

public abstract class Entity {
    public int chunkIndex;
    public double xPos;
    public double yPos;
    public double xVelocity;
    public double yVelocity;
    public double width;
    public double height;
    public boolean interactable = true;
    public Async<Integer> drawableID;
    public Async<Renderer.Drawable> pausedDrawable = null;
    private boolean isPaused = false;
    public abstract void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart);
    public void updateTranslation(double xChunkTranslation, double xChunkVelocity, View view, long tickstart) {
        view.updatePosition(this.drawableID,
                new Vector3f((float) (this.xPos + xChunkTranslation), (float) this.yPos, 0),
                new Vector3f((float) (this.xVelocity + xChunkVelocity), (float) this.yVelocity, 0),
                tickstart);
    }

    public abstract boolean collisionEntEnt(Entity target, Gameloop gameloop);

    public abstract boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target, int chunkOffset);

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
    public void pause(View view){
        if (!this.isPaused) {
            this.isPaused = true;
            this.pausedDrawable = view.getDrawableByID(this.drawableID);
        }
    }
    // Called when reloading an previously paused chunk
    public void unPause(View view){
        if (this.isPaused) {
            this.isPaused = false;
            this.drawableID = view.addToStage(this.pausedDrawable);
        }
    }

    public void moveToChunk(ArrayList<Chunk> chunks, int newChunkIndex, View view) {
        chunks.get(this.chunkIndex).removeEntity(this, view);
        chunks.get(newChunkIndex).addEntity(this, view);

        //this.xPos+=this.chunkIndex<newChunkIndex?-9:9;
        this.xPos += (this.chunkIndex-newChunkIndex)*9;
        this.chunkIndex=newChunkIndex;
    }
}
