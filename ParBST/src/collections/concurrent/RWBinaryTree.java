package collections.concurrent;

import collections.interfaces.Tree;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RWBinaryTree<T extends Comparable<? super T>> extends BinaryTree<T> {
    public class LockableNode extends Tree.Node<T> {
        ReadWriteLock locker;
        LockableNode left;
        LockableNode right;

        public LockableNode(T data) {
            super(data);
            locker = new ReentrantReadWriteLock();
        }

        void readLock() {
            locker.readLock().lock();
        }

        void writeLock() {
            locker.writeLock().lock();
        }

        void readUnlock() {
            locker.readLock().unlock();
        }

        void writeUnLock() {
            locker.writeLock().unlock();
        }
    }

    ReadWriteLock lock;
    LockableNode root;

    public RWBinaryTree() {
        lock = new ReentrantReadWriteLock();
        root = null;
        size = new AtomicInteger(0);
    }
}
