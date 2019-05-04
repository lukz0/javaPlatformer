package Game;

public class Main {
    public static final Renderer.Options INITIAL_RENDERING_OPTIONS = JSONReader.ReadOptions();

    public static void main(String[] args) {
        new Controller();
    }
}