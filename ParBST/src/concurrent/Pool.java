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
        while (Arrays.stream(workers).anyMatch((worker) -> worker.busy));
        terminate = true;
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    public final int size() {
        return queue.size();
    }

    public final boolean isEmpty() {
        return queue.isEmpty();
    }

    private class Worker extends Thread {
        boolean busy = false;

        public void run() {
            Runnable task;

            working: while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            busy = false;
                            if (terminate) break working;
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    busy = true;
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
