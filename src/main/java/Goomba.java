import java.util.HashMap;

public class Goomba implements Movable {
    private static final int STATE_MOVING_RIGHT = 1;
    private static final int STATE_MOVING_LEFT = 2;
    Async<Integer> drawableID;
    Vector3f translation = new Vector3f(16, 0, 0), velocity = new Vector3f(1 * (Gameloop.TICKDURATION/(float)1000), 0, 0);;
    int currentState;

    Goomba(View view, HashMap<String, Async<Texture>> textures, long timestamp) {
        if (!textures.containsKey("primeGoomb_fwd.png")) {
            textures.put("primeGoomb_fwd.png", view.loadTexture("resources/images/primeGoomb_fwd.png"));
        }

        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("primeGoomb_fwd.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("primeGoomb_fwd.png"),
                this.translation, this.velocity, 500, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(Goomba.STATE_MOVING_RIGHT, movingRightSprite);
        states.put(Goomba.STATE_MOVING_LEFT, movingLeftSprite);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = Goomba.STATE_MOVING_RIGHT;
        view.setActiveState(this.drawableID, this.currentState);
    }

    @Override
    public void doMove(Gameloop gameloop, long tickStart) {

        //TODO: add logic regarding choice of direction
        Vector3f nextPos = this.translation.add(this.velocity);
        System.out.println("nextpos:" + nextPos.values[0]);
        if(nextPos.values[0] < 0) {
            this.velocity = new Vector3f(3 * (gameloop.TICKDURATION/(float)1000), 0, 0);
            this.currentState = Goomba.STATE_MOVING_RIGHT;
        }
        else if(nextPos.values[0] > 15) {
            this.velocity = new Vector3f(-3 * (gameloop.TICKDURATION/(float)1000), 0, 0);
            this.currentState = Goomba.STATE_MOVING_LEFT;
        }


        // TODO: replace when we add collisions
        this.translation = this.translation.add(this.velocity);
        gameloop.view.updatePosition(this.drawableID, this.translation, this.velocity, tickStart);
    }
}
