public class View {
    Controller parent;

    View(Controller parent) {
        this.parent = parent;
    }

    Renderer rend;

    public void startRenderer() {
        rend = new Renderer(parent);
        rend.start();
        rend.setBackground(1, 1, 1);
    }

    void setRendererBackground(float r, float g, float b) {
        rend.setBackground(r, g, b);
    }
}
