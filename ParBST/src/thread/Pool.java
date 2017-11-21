package thread;

import java.util.concurrent.LinkedBlockingQueue;

public class Pool {
    private final int nThreads;
    private final Worker[] workers;
    private final LinkedBlockingQueue queue;

    public Pool(int nThreads) {
        this.nThreads = nThreads;
        workers = new Worker[nThreads];
        queue = new LinkedBlockingQueue();

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
        while (!queue.isEmpty());
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
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = (Runnable) queue.poll();
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
