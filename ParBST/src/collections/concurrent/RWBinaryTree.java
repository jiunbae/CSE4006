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
            locker.readLock().lock();
        }

        void unlock() {
            locker.readLock().unlock();
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
            locker.readLock().unlock();
            locker.writeLock().lock();
            try {
                return f.apply(this);
            } finally {
                locker.writeLock().unlock();
                locker.readLock().lock();
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
                }

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
                        break;
                    }
                    next = compare > 0 ? cur.left : cur.right;
                }
                next.lock();
                cur.unlock();
                cur = next;
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
            LockableNode par = null;
            cur.lock();

            int compare = cur.data.compareTo(data);
            // no exception here, cuz holding global lock
            if (compare == 0) {
                if (!cur.write((c) -> {
                    LockableNode itPar = null;
                    LockableNode itCur;
                    if (c.left != null) {
                        itCur = c.left;
                        while (itCur.right != null) {
                            itCur.right.lock();
                            if (itPar != null && itPar != c) itPar.unlock();
                            itPar = itCur;
                            itCur = itCur.right;
                        }

                        if (itPar == null) c.left = itCur.left;
                        else {
                            LockableNode finalItCur = itCur;
                            itPar.write((p) -> {
                                if (p.right == null) {
                                    System.out.println(String.format("thread %d, node %s, DEL: find replacement error %d", Thread.currentThread().getId(), finalItCur.data, p.data));
                                } else
                                    p.right = finalItCur.left;
                                return true;
                            });
                        }
                        c.data = itCur.data;
                        if (itPar != null && itPar != c) itPar.unlock();
                        itCur.unlock();
                    } else if (c.right != null) {
                        itCur = c.right;
                        while (itCur.left != null) {
                            itCur.left.lock();
                            if (itPar != null && itPar != c) itPar.unlock();
                            itPar = itCur;
                            itCur = itCur.left;
                        }

                        if (itPar == null) c.right = itCur.right;
                        else {
                            LockableNode finalItCur = itCur;
                            itPar.write((p) -> {
                                if (p.left == null) {
                                    System.out.println(String.format("thread %d, node %s, DEL: find replacement error %d", Thread.currentThread().getId(), finalItCur.data, p.data));
                                } else
                                    p.left = finalItCur.right;
                                return true;
                            });
                        }
                        c.data = itCur.data;
                        if (itPar != null && itPar != c) itPar.unlock();
                        itCur.unlock();
                    } else {
                        return false;
                    }
                    return true;
                })) {
                    root = null;
                }
                cur.unlock();
                lock.unlock();
            } else {
                lock.unlock();
                while (true) {
                    if (par != null) par.unlock();
                    par = cur;
                    cur = compare > 0 ? cur.left : cur.right;
                    if (cur == null) {
                        par.unlock();
                        return false;
                    }
                    cur.lock();
                    compare = cur.data.compareTo(data);

                    if (compare == 0) {
                        LockableNode finalCur = cur;
                        if (par.write((p) -> {
                            if (finalCur.data.compareTo(data) == 0) {
                                return finalCur.write((c) -> {
                                    LockableNode itPar = null;
                                    LockableNode itCur;
                                    if (c.left != null) {
                                        itCur = c.left;
                                        itCur.lock();
                                        while (itCur.right != null) {
                                            itCur.right.lock();
                                            if (itPar != null && itPar != c) itPar.unlock();
                                            itPar = itCur;
                                            itCur = itCur.right;
                                        }

                                        if (itPar == null) c.left = itCur.left;
                                        else {
                                            LockableNode finalItCur = itCur;
                                            itPar.write((ip) -> {
                                                if (ip.right == null) {
                                                    System.out.println(String.format("thread %d, node %s, DEL: find replacement error %d", Thread.currentThread().getId(), finalItCur.data, p.data));
                                                    return false;
                                                } else
                                                    ip.right = finalItCur.left;
                                                return true;
                                            });
                                        }
                                        c.data = itCur.data;
                                        if (itPar != null && itPar != c) itPar.unlock();
                                        itCur.unlock();
                                    } else if (c.right != null) {
                                        itCur = c.right;
                                        itCur.lock();
                                        while (itCur.left != null) {
                                            itCur.left.lock();
                                            if (itPar != null && itPar != c) itPar.unlock();
                                            itPar = itCur;
                                            itCur = itCur.left;
                                        }

                                        if (itPar == null) c.right = itCur.right;
                                        else {
                                            LockableNode finalItCur = itCur;
                                            itPar.write((ip) -> {
                                                if (ip.left == null) {
                                                    System.out.println(String.format("thread %d, node %s, DEL: find replacement error %d", Thread.currentThread().getId(), finalItCur.data, p.data));
                                                    return false;
                                                } else
                                                    ip.left = finalItCur.right;
                                                return true;
                                            });
                                        }
                                        c.data = itCur.data;
                                        if (itPar != null && itPar != c) itPar.unlock();
                                        itCur.unlock();
                                    } else {
                                        if (p.left == finalCur) p.left = null;
                                        else if (p.right == finalCur) p.right = null;
                                        else {
                                            System.out.println(String.format("thread %d, node %s, DEL: find replacement error %d", Thread.currentThread().getId(), c.data, p.data));
                                            return false;
                                        }
                                    }
                                    return true;
                                });
                            } else {
                                System.out.println(String.format("thread %d, node %s, DEL: assertion failed %s", Thread.currentThread().getId(), finalCur.data, data));
                                return false;
                            }
                        })) {
                            par.unlock();
                            cur.unlock();
                            break;
                        }
                    }
                }
            }
        }
        return true;
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
