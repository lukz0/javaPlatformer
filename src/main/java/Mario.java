class Mario {
    private static final int STATE_MOVING = 1;
    private static final int STATE_IDLE = 2;
    Async<Integer> drawableID;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;

    Mario(Async<Integer> drawableID) {
        this.drawableID = drawableID;
        this.currentState = this.STATE_IDLE;
    }

    public void doMove(Gameloop gameloop, long tickStart) {
        if (gameloop.holdingLeft != gameloop.holdingRight) {
            if (gameloop.holdingLeft) {
                this.velocity = new Vector3f(-1 * (gameloop.TICKDURATION/(float)1000), 0, 0);
            } else {
                this.velocity = new Vector3f(1 * (gameloop.TICKDURATION/(float)1000), 0, 0);
            }
            if (this.currentState != this.STATE_MOVING) {
                this.currentState = this.STATE_MOVING;
                gameloop.view.setActiveState(this.drawableID, this.currentState);
            }
        } else {
            this.velocity = Vector3f.EMPTY;
            if (this.currentState != this.STATE_IDLE) {
                this.currentState = this.STATE_IDLE;
                gameloop.view.setActiveState(this.drawableID, this.currentState);
            }
        }

        // TODO: replace when we add collisions
        this.translation = this.translation.add(this.velocity);
        gameloop.view.updatePosition(this.drawableID, this.translation, this.velocity, tickStart);
    }
}