package Game;

public class Main {
    // 1 for Vsync, 0 for no Vsync
    static final int VSYNC = 1;
    static final boolean FULLSCREEN = false;

    public static final Renderer.Options INITIAL_RENDERING_OPTIONS = JSONReader.ReadOptions();

    public static void main(String[] args) {
        new Controller();
    }
}