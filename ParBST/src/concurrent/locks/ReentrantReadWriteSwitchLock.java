package concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class ReentrantReadWriteSwitchLock implements ReadWriteLock {
    final ReadLock readLock = new ReadLock();
    final WriteLock writeLock = new WriteLock();

    @Override
    public Lock readLock() {
        return null;
    }

    @Override
    public Lock writeLock() {
        return null;
    }

    class SwitchLock implements Lock {

        @Override
        public void lock() {

        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void unlock() {

        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }

    class ReadLock extends SwitchLock {
        ReadLock() {
            super();
        }
    }

    class WriteLock extends SwitchLock {
        WriteLock() {
            super();
        }
    }
}
