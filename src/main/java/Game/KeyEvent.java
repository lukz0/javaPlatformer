package Game;

public class KeyEvent {
    final int key;
    final int action;
    final int modifier;
    KeyEvent(int key, int action, int modifier) {
        this.key = key;
        this.action = action;
        this.modifier = modifier;
    }
}
