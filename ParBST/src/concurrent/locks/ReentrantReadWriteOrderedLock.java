package concurrent.locks;

import collections.LinkedList;
import collections.interfaces.List;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class ReentrantReadWriteOrderedLock implements ReadWriteLock {
    final List<Map.Entry<Long, LockType>> waiter;
    final Lock mutex;
    final Condition reader;
    final Condition writer;

    boolean writing;
    int readers;
    List.Node it;

    final ReadLock readLock = new ReadLock();
    final WriteLock writeLock = new WriteLock();

    public ReentrantReadWriteOrderedLock() {
        waiter = new LinkedList<>();
        mutex = new ReentrantLock();
        reader = mutex.newCondition();
        writer = mutex.newCondition();

        writing = false;
        readers = 0;
        it = null;
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
                it = waiting();
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

        List.Node waiting() {
            return waiter.addNode(new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type));
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
                waiting();
                afterLock();
                return true;
            } finally {
                mutex.unlock();
            }
        }

        @Override
        public void unlock() {
            int local_readers;
            boolean local_writing;

            mutex.lock();
            it.remove();
            local_readers = --readers;
            local_writing = writing;
            try {
            } finally {
                mutex.unlock();
            }

            if (local_writing && local_readers == 0)
                reader.signalAll();
            else if (!local_writing)
                writer.signalAll();
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
                waiting();
                return writing = true;
            } finally {
                mutex.unlock();
            }
        }

        @Override
        public void unlock() {
            mutex.lock();
            try {
                writing = false;
                it.remove();
            } finally {
                mutex.unlock();
            }
        }

        @Override
        boolean validate() {
            return waiter.get(0).equals(new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type));
        }
    }
}
