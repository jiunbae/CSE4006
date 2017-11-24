package concurrent.locks;

import collections.LinkedList;
import collections.interfaces.List;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class ReentrantReadWriteOrderedLock implements ReadWriteLock {
    private final ConcurrentLinkedQueue<Map.Entry<Long, LockType>> waiter;
    private final Lock mutex;
    private final Condition reader;
    private final Condition writer;
    private final Map<Long, Map.Entry<Long, LockType>> entries;

    private boolean writing;
    private int readers;

    private final ReadLock readLock = new ReadLock();
    private final WriteLock writeLock = new WriteLock();

    public ReentrantReadWriteOrderedLock() {
        waiter = new ConcurrentLinkedQueue<>();
        mutex = new ReentrantLock();
        reader = mutex.newCondition();
        writer = mutex.newCondition();
        entries = new HashMap<>();

        writing = false;
        readers = 0;
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    enum LockType {
        READ, WRITE
    }

    abstract class OrderedLock implements Lock {

        final LockType type;

        OrderedLock(LockType type) {
            this.type = type;
        }

        @Override
        public void lock() {
            mutex.lock();
            try {
                Map.Entry<Long, LockType> entry = new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type);
                entries.put(Thread.currentThread().getId(), entry);
                waiter.add(entry);
                do {
                    while (!validate())
                        writer.await();
                } while (writing);
                afterLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mutex.unlock();
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException("This lock does not support method");
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException("This lock does not support method");
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("This lock does not support method");
        }

        abstract void afterLock() throws InterruptedException;

        abstract boolean validate();
    }

    class ReadLock extends OrderedLock {
        ReadLock() {
            super(LockType.READ);
        }

        @Override
        public void afterLock() {
            readers += 1;
        }

        @Override
        public boolean tryLock() {
            mutex.lock();
            try {
                if (writing) return false;
                waiter.offer(new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type));
                afterLock();
                return true;
            } finally {
                mutex.unlock();
            }
        }

        @Override
        public void unlock() {
            mutex.lock();
            try {
                waiter.remove(entries.remove(Thread.currentThread().getId()));
                --readers;
                if (writing && readers == 0)
                    reader.signalAll();
                else if (!writing)
                    writer.signalAll();
            } catch (IllegalMonitorStateException e) {
                e.printStackTrace();
            } finally {
                mutex.unlock();
            }
        }

        @Override
        boolean validate() {
            for (Map.Entry<Long, LockType> entry : waiter) {
                if (!entry.getValue().equals(type)) break;
                else if (entry.getKey().equals(Thread.currentThread().getId())) return true;
            }
            return false;
        }
    }

    class WriteLock extends OrderedLock {
        WriteLock() {
            super(LockType.WRITE);
        }

        @Override
        public void afterLock() throws InterruptedException {
            writing = true;
            while (readers > 0)
                reader.await();
        }

        @Override
        public boolean tryLock() {
            mutex.lock();
            try {
                if (writing || readers > 0) return false;
                waiter.offer(new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type));
                return writing = true;
            } finally {
                mutex.unlock();
            }
        }

        @Override
        public void unlock() {
            mutex.lock();
            try {
                waiter.remove(entries.remove(Thread.currentThread().getId()));
                writing = false;

                writer.signal();
            } finally {
                mutex.unlock();
            }

        }

        @Override
        boolean validate() {
            return waiter.peek().equals(new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type));
        }
    }
}
