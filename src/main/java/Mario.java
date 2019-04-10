import java.util.HashMap;

class Mario {
    private static final int STATE_MOVING_RIGHT = 1;
    private static final int STATE_MOVING_LEFT = 2;
    private static final int STATE_IDLE_RIGHT = 3;
    private static final int STATE_IDLE_LEFT = 4;
    Async<Integer> drawableID;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;

    Mario(View view, HashMap<String, Async<Texture>> textures, long timestamp) {
        if (!textures.containsKey("mario_right.png")) {
            textures.put("mario_right.png", view.loadTexture("resources/images/mario_right.png"));
        }
        if (!textures.containsKey("mario_stand.png")) {
            textures.put("mario_stand.png", view.loadTexture("resources/images/mario_stand.png"));
        }
        /*if (!textures.containsKey("mario_jump.png")) {
            textures.put("mario_jump.png", view.loadTexture("resources/images/mario_jump.png"));
        }*/
        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 2, 1, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(1, 0, 2, 1, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> marioIdleRight = view.getNewTexturedRectangle(0, 1, 2, 1, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioIdleLeft = view.getNewTexturedRectangle(1, 0, 2, 1, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(this.STATE_MOVING_RIGHT, movingRightSprite);
        states.put(this.STATE_MOVING_LEFT, movingLeftSprite);
        states.put(this.STATE_IDLE_RIGHT, marioIdleRight);
        states.put(this.STATE_IDLE_LEFT, marioIdleLeft);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = this.STATE_IDLE_RIGHT;
        view.setActiveState(this.drawableID, this.currentState);
    }

    public void doMove(Gameloop gameloop, long tickStart) {
        if (gameloop.holdingLeft != gameloop.holdingRight) {
            if (gameloop.holdingLeft) {
                this.velocity = new Vector3f(-3 * (gameloop.TICKDURATION/(float)1000), 0, 0);
                if (this.currentState != this.STATE_MOVING_LEFT) {
                    this.currentState = this.STATE_MOVING_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            } else {
                this.velocity = new Vector3f(3 * (gameloop.TICKDURATION/(float)1000), 0, 0);
                if (this.currentState != this.STATE_MOVING_RIGHT) {
                    this.currentState = this.STATE_MOVING_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            }
        } else {
            this.velocity = Vector3f.EMPTY;
            //if (this.currentState != this.STATE_IDLE_RIGHT && this.currentState != this.STATE_IDLE_LEFT) {
                if(this.currentState == this.STATE_MOVING_LEFT) {
                    this.currentState = this.STATE_IDLE_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
                else if (this.currentState == this.STATE_MOVING_RIGHT) {
                    this.currentState = this.STATE_IDLE_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            //}
        }

        // TODO: replace when we add collisions
        this.translation = this.translation.add(this.velocity);
        gameloop.view.updatePosition(this.drawableID, this.translation, this.velocity, tickStart);
    }
}