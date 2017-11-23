package collections.concurrent;

import collections.interfaces.Tree;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RWBinaryTree<T extends Comparable<? super T>> implements Tree<T> {
    public class LockableNode extends Tree.Node<T> {
        final ReadWriteLock locker;
        List<Long> writers;
        int readers;
        LockableNode left;
        LockableNode right;

        public LockableNode(T data) {
            super(data);
            locker = new ReentrantReadWriteLock();
            writers = new LinkedList<>();
            readers = 0;
        }

        void lock() {
            System.out.println(String.format("tid: %d, item: %s, read: try to read", Thread.currentThread().getId(), this.data));
            synchronized (writers) {
                System.out.println(String.format("tid: %d, item: %s, read: wait writing", Thread.currentThread().getId(), this.data));
                while (!writers.isEmpty());
                System.out.println(String.format("tid: %d, item: %s, read: waiting done", Thread.currentThread().getId(), this.data));
            }
            locker.readLock().lock();
            System.out.println(String.format("tid: %d, item: %s, read: acquire read", Thread.currentThread().getId(), this.data));
            readers += 1;
            System.out.println(String.format("now read: %d", readers));
        }

        void unlock() {
            System.out.println(String.format("tid: %d, item: %s, read: try to unlock read", Thread.currentThread().getId(), this.data));
            locker.readLock().unlock();
            System.out.println(String.format("tid: %d, item: %s, read: release read", Thread.currentThread().getId(), this.data));
            readers -= 1;
            System.out.println(String.format("now read: %d", readers));
        }

        void write(final Consumer<LockableNode> f) {
            unlock();
            synchronized (writers) {
                System.out.println(String.format("tid: %d, item: %s, write: try to write", Thread.currentThread().getId(), this.data));
                writers.add(Thread.currentThread().getId());
                while (!writers.get(0).equals(Thread.currentThread().getId()));
                System.out.println(String.format("tid: %d, item: %s, write:  acquire write", Thread.currentThread().getId(), this.data));
            }
            while (readers > 0);
            locker.writeLock().lock();
            try {
                System.out.println(String.format("tid: %d, item: %s, write:  start write", Thread.currentThread().getId(), this.data));
                f.accept(this);
            } finally {
                synchronized (writers) {
                    writers.remove(0);
                }
                locker.writeLock().unlock();
                lock();
                System.out.println(String.format("tid: %d, item: %s, write:  write done", Thread.currentThread().getId(), this.data));
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
                        cur.write((c) -> {
                            System.out.println(String.format("tid: %d, item: %s write", Thread.currentThread().getId(), data));
                            if (compare > 0) c.left = new LockableNode(data);
                            else c.right = new LockableNode(data);
                        });
                        cur.unlock();
                        break;
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
            if (compare != 0) {
                par = cur;
                cur = compare > 0 ? cur.left : cur.right;
                cur.lock();
                lock.unlock();

                while (true) {
                    compare = cur.data.compareTo(data);
                    if (compare == 0) {
                        LockableNode rep = replacement(cur);

                        int finalCompare = par.data.compareTo(data);
                        par.write((p) -> {
                            if (finalCompare > 0) p.left = rep;
                            else p.right = rep;
                        });

                        if (rep != null) {
                            LockableNode finalCur = cur;
                            rep.write((r) -> {
                                r.left = finalCur.left;
                                r.right= finalCur.right;
                            });
                        }

                        cur.unlock();
                        par.unlock();
                        break;
                    } else {
                        par.unlock();
                        par = cur;

                        compare = cur.data.compareTo(data);
                        if (compare > 0) cur = cur.left;
                        else cur = cur.right;
                    }

                    if (cur == null) return false;
                    else cur.lock();
                }
            } else {
                LockableNode rep = replacement(cur);
                root = rep;

                if (rep != null) {
                    LockableNode finalCur = cur;
                    rep.lock();
                    rep.write((r) -> {
                        r.left = finalCur.left;
                        r.right= finalCur.right;
                    });
                }

                cur.unlock();
                lock.unlock();
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
            LockableNode finalCur = cur;
            par.write((p) -> {
               finalCur.write((c) -> {
                   if (p == sub) p.left = c.left;
                   else p.right = c.left;
               });
            });
            if (par != sub) par.unlock();
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
            LockableNode finalCur = cur;
            par.write((p) -> {
                finalCur.write((c) -> {
                    if (p == sub) p.right = c.right;
                    else p.left = c.right;
                });
            });
            if (par != sub) par.unlock();
            if (cur.right != null) cur.right.unlock();

            cur.unlock();

        } else {
            return null;
        }

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
