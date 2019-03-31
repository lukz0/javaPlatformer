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
        Timer timer = new Timer();

        //performs the tick every 20 millisecond, should be about 50 times per second.
        //NOTE:the task will always perform 50 times a second, could possibly lead to uneven tick times
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, 0, 20);
    }

    //command that runs 50 times a second, consider performance when writing code here
    void tick() {
        //TODO: write what the game loop will actually do
        return;
    }


}
