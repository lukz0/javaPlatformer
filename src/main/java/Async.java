import java.util.concurrent.ArrayBlockingQueue;

class Async<T> {
    T value;
    ArrayBlockingQueue<T> queue;

    Async(ArrayBlockingQueue<T> queue) {
        this.queue = queue;
    }

    T get() {
        if (this.queue != null) {
            try {
                this.value = this.queue.take();
                this.queue = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.value;
    }
}