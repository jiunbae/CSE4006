package concurrent.locks;

import javafx.util.Pair;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteLock {
    private final ConcurrentLinkedQueue<Pair<Long, Boolean>> waiter;
    private final Lock lock;
    private final Condition reader;
    private final Condition writer;

    private boolean writing;
    private long readers;

    public ReadWriteLock() {
        waiter = new ConcurrentLinkedQueue<>();
        lock = new ReentrantLock();
        reader = lock.newCondition();
        writer = lock.newCondition();

        writing = false;
        readers = 0;
    }

    public void readLock() {
        lock.lock();

        try {
            waiter.add(new Pair<>(Thread.currentThread().getId(), false));
            do {
                try {
                    writer.wait();
                } catch (InterruptedException e) {
                    for (Pair<Long, Boolean> p : waiter) {
                        if (p.getValue()) break;
                        else if (p.equals(new Pair<>(Thread.currentThread(), false))) return true;
                    }
                }
            } while (writing);
        } finally {
            readers += 1;
            lock.unlock();
        }
    }

    public void readUnlock() {

    }

    public void writeLock() {

    }

    public void writeUnlock() {

    }
}
