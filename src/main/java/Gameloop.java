import java.util.Timer;
import java.util.TimerTask;

public class Gameloop implements Runnable {
    private Thread thread;


    void start() {
        if (thread == null) {
            thread = new Thread(this, "gameloop thread");
            this.thread.start();
        }
    }

    public void run() {
        while (true) {
            long tickStart = System.nanoTime();

            // TODO: game logic
            // Use tickStart as timestamp argument to View methods

            long tickEnd = System.nanoTime();
            try {
                Thread.sleep(Controller.tickDuration-((tickEnd-tickStart)/1000000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
