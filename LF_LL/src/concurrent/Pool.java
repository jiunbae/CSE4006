package concurrent;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class Pool {
    private boolean terminate;
    private final Worker[] workers;
    private final LinkedBlockingQueue<Runnable> queue;

    public Pool(int nThreads) {
        workers = new Worker[nThreads];
        queue = new LinkedBlockingQueue<>();
        terminate = false;

        for (int i = 0; i < nThreads; ++i) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    public void push(Runnable task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    public void join() {
        terminate = true;
        for (Worker worker : workers)
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public final int size() {
        return queue.size();
    }

    public final boolean isEmpty() {
        return queue.isEmpty();
    }

    private class Worker extends Thread {
        public void run() {
            Runnable task;

            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            if (queue.isEmpty() && terminate)
                                return;
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = queue.poll();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
