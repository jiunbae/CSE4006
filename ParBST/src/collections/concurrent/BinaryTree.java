package collections.concurrent;

import collections.interfaces.Tree;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.concurrent.locks.ReentrantLock;

public class BinaryTree<T extends Comparable<? super T>> implements collections.interfaces.Tree<T> {
    public class LockableNode extends Tree.Node<T> {
        ReentrantLock locker;
        LockableNode left;
        LockableNode right;

        public LockableNode(T data) {
            super(data);
            locker = new ReentrantLock();
        }

        void lock() {
            locker.lock();
        }

        void unlock() {
            locker.unlock();
        }
    }

    ReentrantLock lock;
    LockableNode root;
    AtomicInteger size;

    public BinaryTree() {
        lock = new ReentrantLock();
        root = null;
        size = new AtomicInteger(0);
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
                        if (compare > 0) cur.left = new LockableNode(data);
                        else cur.right = new LockableNode(data);
                        cur.unlock();
                        break;
                    }

                    next.lock();
                    cur.unlock();
                    cur = next;
                }
            }
        }
        size.incrementAndGet();
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
                    rep.left = cur.left;
                    rep.right = cur.right;
                }

                cur.unlock();
                lock.unlock();
            }

        }
        size.decrementAndGet();
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

        } else {
            return null;
        }

        return cur;
    }

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
    public int size() {
        return this.size.get();
    }

    @Override
    public void preOrderTraversal(final Consumer<Node<T>> f) {
        lock.lock();
        preOrderHelper(root, f);
        lock.unlock();
    }

    private void preOrderHelper(Node r, final Consumer<Node<T>> f) {
        if (r == null) return;
        f.accept(r);
        preOrderHelper(r.left, f);
        preOrderHelper(r.right, f);
    }

    @Override
    public void inOrderTraversal(final Consumer<Node<T>> f) {
        lock.lock();
        inOrderHelper(root, f);
        lock.unlock();
    }

    private void inOrderHelper(Node r, final Consumer<Node<T>> f) {
        if (r == null) return;
        inOrderHelper(r.left, f);
        f.accept(r);
        inOrderHelper(r.right, f);
    }
}
