package collections.concurrent;

import collections.interfaces.Tree;
import concurrent.locks.ReentrantReadWriteOrderedLock;

import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class RWBinaryTree<T extends Comparable<? super T>> implements Tree<T> {
    public class LockableNode extends Tree.Node<T> {
        LockableNode left;
        LockableNode right;

        final ReadWriteLock locker;

        public LockableNode(T data) {
            super(data);
            locker = new ReentrantReadWriteOrderedLock();
        }

        void lock() {
            System.out.println(String.format("thread %d, node %s, READ: request lock", Thread.currentThread().getId(), data));
            locker.readLock().lock();
            System.out.println(String.format("thread %d, node %s, READ: acquire lock", Thread.currentThread().getId(), data));
        }

        void unlock() {
            System.out.println(String.format("thread %d, node %s, READ: request release", Thread.currentThread().getId(), data));
            locker.readLock().unlock();
            System.out.println(String.format("thread %d, node %s, READ: accept release", Thread.currentThread().getId(), data));
        }

        /**
         * Simplifies read scope.
         * It is safer than calling {@link LockableNode#lock()} and {@link LockableNode#unlock()} directly.
         *
         * @param f Return value T, which applied
         * @return T which return from f
         */
        T read(final Function<LockableNode, T> f) {
            locker.readLock().lock();
            try {
                return f.apply(this);
            } finally {
                locker.readLock().unlock();
            }
        }

        /**
         * Critical section of {@link LockableNode}
         * In write action, must unlock read and wait for write lock.
         * If acquire write lock, then do action and release write lock.
         * Finally re-acquire reader locker for safety.
         *
         * @param f Return false raise dirty write when acquire write lock,
         *          must configure logic to re-try, else return true
         */
        boolean write(final Function<LockableNode, Boolean> f) {
            System.out.println(String.format("thread %d, node %s, WRITE: request read release for write", Thread.currentThread().getId(), data));
            locker.readLock().unlock();
            System.out.println(String.format("thread %d, node %s, WRITE: accept read release for write", Thread.currentThread().getId(), data));
            System.out.println(String.format("thread %d, node %s, WRITE: request lock", Thread.currentThread().getId(), data));
            locker.writeLock().lock();
            System.out.println(String.format("thread %d, node %s, WRITE: acquire lock", Thread.currentThread().getId(), data));
            try {
                System.out.println(String.format("thread %d, node %s, WRITE: write start", Thread.currentThread().getId(), data));
                return f.apply(this);
            } finally {
                System.out.println(String.format("thread %d, node %s, WRITE: write done", Thread.currentThread().getId(), data));
                System.out.println(String.format("thread %d, node %s, WRITE: request release", Thread.currentThread().getId(), data));
                locker.writeLock().unlock();
                System.out.println(String.format("thread %d, node %s, WRITE: accept release", Thread.currentThread().getId(), data));
                System.out.println(String.format("thread %d, node %s, WRITE: request lock read", Thread.currentThread().getId(), data));
                locker.readLock().lock();
                System.out.println(String.format("thread %d, node %s, WRITE: acquire lock read", Thread.currentThread().getId(), data));
            }
        }
    }

    private final Lock lock;
    private LockableNode root;

    public RWBinaryTree() {
        lock = new ReentrantLock();
        root = null;
    }

    @Override
    public boolean insert(T data) {
        lock.lock();
        if (root == null) {
            root = new LockableNode(data);
            lock.unlock();
        } else {
            LockableNode cur = root;
            cur.lock();
            lock.unlock();

            while (true) {
                int compare = cur.data.compareTo(data);
                if (compare == 0) {
                    cur.unlock();
                    return false;
                } else {
                    LockableNode next = compare > 0 ? cur.left : cur.right;
                    if (next == null) {
                        if (cur.write((c) -> {
                            if (compare > 0)
                                if (c.left != null) return false;
                                else c.left = new LockableNode(data);
                            else
                                if (c.right != null) return false;
                                else c.right = new LockableNode(data);
                            return true;
                        })) {
                            cur.unlock();
                            System.out.println(String.format("thread %d, node %s, WRITE: write %d", Thread.currentThread().getId(), cur.data, data));
                            break;
                        }
                        System.out.println(String.format("thread %d, node %s, assertion failed, retry", Thread.currentThread().getId(), cur.data));
                        next = compare > 0 ? cur.left : cur.right;
                    }
                    next.lock();
                    cur.unlock();
                    cur = next;
                }
            }
        }
        return true;
    }

    @Override
    public boolean delete(T data) {
        lock.lock();
        if (root == null) {
            lock.unlock();
        } else {
            LockableNode cur = root;
            LockableNode par;
            cur.lock();

            int compare = cur.data.compareTo(data);
            if (compare == 0) {
                LockableNode rep = replacement(cur);
                root = rep;

                // It doesn't fail assertion, cuz holding global lock
                if (rep != null)
                    rep.write((r) -> {
                        r.left = root.left;
                        r.right = root.right;
                        return true;
                    });

                cur.unlock();
                lock.unlock();
            } else {
                par = cur;
                cur = compare > 0 ? cur.left : cur.right;
                cur.lock();
                lock.unlock();

                while (true) {
                    compare = cur.data.compareTo(data);
                    if (compare == 0) {
                        par.write((p) -> {
                            
                            return true;
                        });
                        LockableNode rep = replacement(cur);

                        compare = par.data.compareTo(data);
                        if (compare > 0) par.left = rep;
                        else par.right = rep;

                        if (rep != null) {
                            rep.left = cur.left;
                            rep.right = cur.right;
                        }

                        cur.unlock();
                        par.unlock();
                        break;
                    } else {
                        par.unlock();
                        par = cur;
                        cur = compare > 0 ? cur.left : cur.right;
                    }

                    if (cur == null) return false;
                    else cur.lock();
                }
            }

        }
        return true;
    }

    private LockableNode replacement(LockableNode sub) {
        LockableNode cur;
        LockableNode par = sub;

        if (sub.left != null) {
            cur = sub.left;
            cur.lock();

            while (cur.right != null) {
                if (par != sub) par.unlock();
                par = cur;
                cur = cur.right;
                cur.lock();
            }

            if (cur.left != null) cur.left.lock();
            if (par == sub) par.left = cur.left;
            else {
                par.right = cur.left;
                par.unlock();
            }
            if (cur.left != null) cur.left.unlock();

            cur.unlock();
        } else if (sub.right != null) {
            cur = sub.right;
            cur.lock();

            while (cur.left != null) {
                if (par != sub) par.unlock();
                par = cur;
                cur = cur.left;
                cur.lock();
            }

            if (cur.right != null) cur.right.lock();
            if (par == sub) par.right = cur.right;
            else {
                par.left = cur.right;
                par.unlock();
            }
            if (cur.right != null) cur.right.unlock();

            cur.unlock();
        } else return null;
        return cur;
    }

    @Override
    public boolean search(T data) {
        lock.lock();
        if (root == null) {
            lock.unlock();
            return false;
        } else {
            LockableNode cur = root;
            cur.lock();
            lock.unlock();

            while (true) {
                int compare = cur.data.compareTo(data);
                if (compare == 0) {
                    cur.unlock();
                    return true;
                } else {
                    LockableNode next = compare > 0 ? cur.left : cur.right;
                    if (next == null) {
                        cur.unlock();
                        return false;
                    }

                    next.lock();
                    cur.unlock();
                    cur = next;
                }
            }
        }
    }

    @Override
    public void preOrderTraversal(final Consumer<Node<T>> f) {
        lock.lock();
        if (root == null) {
            lock.unlock();
            return;
        }
        root.lock();
        preOrderHelper(root, f);
        lock.unlock();
    }

    private void preOrderHelper(LockableNode node, final Consumer<Node<T>> f) {
        f.accept(node);
        node.unlock();

        if (node.left != null) {
            node.left.lock();
            preOrderHelper(node.left, f);
        }

        if (node.right != null) {
            node.right.lock();
            preOrderHelper(node.right, f);
        }
    }

    @Override
    public void inOrderTraversal(final Consumer<Node<T>> f) {
        lock.lock();
        if (root == null) {
            lock.unlock();
            return;
        }
        root.lock();
        inOrderHelper(root, f);
        lock.unlock();
    }

    private void inOrderHelper(LockableNode node, final Consumer<Node<T>> f) {
        if (node.left != null) {
            node.left.lock();
            inOrderHelper(node.left, f);
        }

        f.accept(node);
        node.unlock();

        if (node.right != null) {
            node.right.lock();
            inOrderHelper(node.right, f);
        }
    }
}
