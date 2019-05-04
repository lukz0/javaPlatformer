package Game;

import Level.Block.AbstractBlock;
import Level.Chunk;
import Level.Entity;
import Level.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class Brick extends Entity {
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    private static final int STATE_IDLE = 1;
    private static final int STATE_BROKEN = 2;
    int currentState;
    Level level;
    private HashMap<String, Async<Texture>> textures;

    public Brick(View view, HashMap<String, Async<Texture>> textures, long timestamp, double xPos, double yPos, int chunkIndex, Level level) {
        this.textures = textures;
        this.width = 1;
        this.height = 1;
        this.level = level;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.chunkIndex = chunkIndex;
        if (!textures.containsKey("brick.png")) {
            textures.put("brick.png", view.loadTexture("resources/images/brick.png"));
        }
        if (!textures.containsKey("brickHit.png")) {
            textures.put("brickHit.png", view.loadTexture("resources/images/brickHit.png"));
        }
        if (!textures.containsKey("coin.png")) {
            textures.put("coin.png", view.loadTexture("resources/images/coin.png"));
        }

        Async<Renderer.Drawable> idleSprite = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("brick.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> brokenSprite = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("brickHit.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(Game.Brick.STATE_IDLE, idleSprite);
        states.put(Brick.STATE_BROKEN, brokenSprite);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = Brick.STATE_IDLE;
        view.setActiveState(this.drawableID, this.currentState);
    }

    @Override
    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
        if(!this.interactable) {
            this.currentState = Brick.STATE_BROKEN;
            gameloop.view.setActiveState(this.drawableID, this.currentState);
        }
    }

    @Override
    public boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target, int chunkOffset) {
        return false;
    }

    @Override
    public void updatePos() {}

    @Override
    public boolean collisionEntEnt(Entity target, Gameloop gameloop, int chunkOffset) {
        return false;
    }

    private float currentCoinTranslation = 0f;
    private static final float COIN_SPEED = 0.1f;
    private boolean coinStarted = false;
    private boolean coinStopped = false;
    private Async<Integer> coinSpriteID;
    @Override
    public void updateTranslation(double xChunkTranslation, double xChunkVelocity, View view, long tickstart) {
        super.updateTranslation(xChunkTranslation, xChunkVelocity, view, tickstart);
        if (this.currentState == STATE_BROKEN) {
            if (!coinStarted) {
                coinStarted = true;
                this.coinSpriteID = view.createTexturedRectangle(0, 1, 1, 0, -0.49f, textures.get("coin.png"),
                        new Vector3f((float)(this.xPos + xChunkTranslation), (float) this.yPos+currentCoinTranslation, 0),
                        new Vector3f((float) (this.xVelocity + xChunkVelocity), (float) this.yVelocity+COIN_SPEED, 0), tickstart);
            } else if (currentCoinTranslation < 1f) {
                currentCoinTranslation += COIN_SPEED;
                view.updatePosition(this.coinSpriteID, new Vector3f((float)(this.xPos + xChunkTranslation), (float) this.yPos+currentCoinTranslation, 0),
                        new Vector3f((float) (this.xVelocity + xChunkVelocity), (float) this.yVelocity+COIN_SPEED, 0), tickstart);
            } else if (!coinStopped) {
                coinStopped = true;
                view.deleteDrawable(this.coinSpriteID);
            }
        }
    }

    private Async<Renderer.Drawable> pausedCoin = null;
    @Override
    public void pause(View view){
        if (!super.isPaused && !coinStopped && coinStarted) {
            this.pausedCoin = view.getDrawableByID(this.coinSpriteID);
        }
        super.pause(view);
    }
    @Override
    public void unPause(View view){
        if (super.isPaused && !coinStopped && coinStarted) {
            this.coinSpriteID = view.addToStage(this.pausedCoin);
        }
        super.unPause(view);
    }
}
