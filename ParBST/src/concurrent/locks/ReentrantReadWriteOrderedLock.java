package concurrent.locks;

import javafx.util.Pair;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ReentrantReadWriteOrderedLock implements  ReadWriteOrderedLock{
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    final Lock updateMutex = new ReentrantLock();

    final ReadLock readLock = new ReadLock();
    final WriteLock writeLock = new WriteLock();

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
        final Lock mutex = new ReentrantLock();
        final Condition reader = mutex.newCondition();
        final Condition writer = mutex.newCondition();
        final ConcurrentLinkedQueue<Map.Entry<Long, LockType>> waiter
                = new ConcurrentLinkedQueue<>();

        boolean writing = false;
        int readers = 0;

        final LockType type;

        public OrderedLock(LockType type) {
            this.type = type;
        }

        @Override
        public void lock() {
            synchronized (writer) {
                try {
                    waiter.add(new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type));
                    do {
                        while (!validate())
                            writer.wait();
                    } while (writing);
                    afterLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException("This lock does not support method");
        }

        @Override
        public boolean tryLock() {
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
        public void unlock() {
            int local_readers;
            boolean local_writing;

            synchronized (waiter) {
                for (Iterator<Map.Entry<Long, LockType>> it = waiter.iterator(); it.hasNext();) {
                    Map.Entry<Long, LockType> entry = it.next();
                    if (entry.getValue().equals(LockType.WRITE)) break;
                    it.remove();
                }
                local_readers = -- readers;
                local_writing = writing;
            }

            if (waiter.isEmpty()) return;
            if (local_writing && local_readers == 0) reader.notifyAll();
            else if (!local_writing) writer.notify();
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
            synchronized (reader) {
                while (readers > 0)
                    reader.wait();
            }
        }

        @Override
        public void unlock() {
            synchronized (waiter) {
                writing = false;
                for (Iterator<Map.Entry<Long, LockType>> it = waiter.iterator(); it.hasNext();) {
                    Map.Entry<Long, LockType> entry = it.next();
                    if (entry.getValue().equals(type)) {
                        it.remove();
                        break;
                    }
                }
            }

            if (!waiter.isEmpty()) writer.notifyAll();
        }

        @Override
        boolean validate() {
            return waiter.peek().equals(new AbstractMap.SimpleEntry<>(Thread.currentThread().getId(), type));
        }
    }
}
