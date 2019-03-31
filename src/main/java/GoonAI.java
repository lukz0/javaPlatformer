public class GoonAI {
    Enemy parent;

    GoonAI() {

    }

    GoonAI(Enemy parent) {
        this.parent = parent;

        parent.vX = -parent.spd; // Some constant, needs tweaking
    }

    void attack() {
        if (true) { // TODO: check for parent and wall/other enemy collision
            parent.vX *= -1;
        }
        parent.doMove(); // Or whatever the equivalent function is that makes the entity actually move
    }
}
