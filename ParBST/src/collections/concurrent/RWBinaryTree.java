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
         * @param f
         */
        void write(final Consumer<LockableNode> f) {
            System.out.println(String.format("thread %d, node %s, WRITE: request read release for write", Thread.currentThread().getId(), data));
            locker.readLock().unlock();
            System.out.println(String.format("thread %d, node %s, WRITE: accept read release for write", Thread.currentThread().getId(), data));
            System.out.println(String.format("thread %d, node %s, WRITE: request lock", Thread.currentThread().getId(), data));
            locker.writeLock().lock();
            System.out.println(String.format("thread %d, node %s, WRITE: acquire lock", Thread.currentThread().getId(), data));
            try {
                System.out.println(String.format("thread %d, node %s, WRITE: write start", Thread.currentThread().getId(), data));
                f.accept(this);
                System.out.println(String.format("thread %d, node %s, WRITE: write done", Thread.currentThread().getId(), data));
            } finally {
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
            LockableNode par = null;
            cur.lock();
            lock.unlock();

            while (true) {
                int compare = cur.data.compareTo(data);
                if (compare == 0) {
                    if (par != null) par.unlock();
                    cur.unlock();
                    return false;
                }
                LockableNode next = compare > 0 ? cur.left : cur.right;
                if (next == null) {
                    LockableNode finalCur = cur;
                    if (par != null) {
                        par.write((p) ->
                                finalCur.write((c) -> {
                                    if (compare > 0) c.left = new LockableNode(data);
                                    else c.right = new LockableNode(data);
                                })
                        );
                    } else {
                        cur.write((c) -> {
                            if (compare > 0) c.left = new LockableNode(data);
                            else c.right = new LockableNode(data);
                        });
                    }
                    cur.unlock();
                    if (par != null) par.unlock();
                    break;
                }

                next.lock();
                if (par != null) par.unlock();
                par = cur;
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
            int compare;
            cur.lock();

            compare = cur.data.compareTo(data);
            if (compare == 0) {
                T value = findReplacement(cur);
                if (value == null) root = null;
                else cur.write((c) -> c.data = value);
                cur.unlock();
                lock.unlock();
            } else {
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
                        T value = findReplacement(cur);

                        LockableNode finalCur = cur;
                        par.write((p) -> {
                           if (value == null) {
                               if (p.left == finalCur) p.left = null;
                               else p.right = null;
                           } else finalCur.write((c) -> c.data = value);
                        });

                        par.unlock();
                        cur.unlock();
                        break;
                    }
                }

            }


        }
        return true;
    }

    private T findReplacement(LockableNode sub) {
        LockableNode par = sub;

        if (sub.left != null) {
            LockableNode cur = sub.left;
            cur.lock();

            while (cur.right != null) {
                if (par != sub) par.unlock();
                par = cur;
                cur = cur.right;
                cur.lock();
            }

            LockableNode finalCur = cur;
            par.write((p) -> {
                if (sub.left == finalCur) p.left = finalCur.left;
                else p.right = finalCur.left;
            });
            T value = cur.data;
            cur.unlock();
            par.unlock();
            return value;
        } else if (sub.right != null) {
            return sub.right.read((r) -> {
                sub.write((s) -> {
                    sub.left = sub.right.left;
                    sub.right = sub.right.right;
                });
                return r.data;
            });
        }
        return null;
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
