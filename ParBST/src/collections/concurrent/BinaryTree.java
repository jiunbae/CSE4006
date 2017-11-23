package collections.concurrent;

import collections.interfaces.Tree;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
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

    /**
     * Acquire ReadLock
     * @param node
     */
    private void acquireGuard(LockableNode node) {
        node.lock();
    }

    private void releaseGuard(LockableNode node) {
        node.unlock();
    }

    /**
     * Acquire WriterLock
     * @param node
     */
    private void acquireBarrier(LockableNode node) {
        node.lock();
    }

    private void releaseBarrier(LockableNode node) {
        node.unlock();
    }

    /**
     * Acquire WriterLock on ReaderLock
     * @param node
     */
    private void acquireBarrierOverGuard(LockableNode node) {

    }

    private void releaseBarrierOverGuard(LockableNode node) {

    }

    @Override
    public boolean insert(T data) {
        lock.lock();
        if (root == null) {
            root = new LockableNode(data);
            lock.unlock();
        } else {
            LockableNode cur = root;
            acquireGuard(cur);
            lock.unlock();

            while (true) {
                int compare = cur.data.compareTo(data);
                if (compare == 0) {
                    releaseGuard(cur);
                    return false;
                } else {
                    LockableNode next = compare > 0 ? cur.left : cur.right;
                    if (next == null) {
                        acquireBarrierOverGuard(cur);
                        if (compare > 0) cur.left = new LockableNode(data);
                        else cur.right = new LockableNode(data);
                        releaseBarrier(cur);
                        break;
                    }

                    acquireGuard(next);
                    releaseGuard(cur);
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
            acquireGuard(cur);

            int compare = cur.data.compareTo(data);
            if (compare != 0) {
                par = cur;
                cur = compare > 0 ? cur.left : cur.right;
                acquireGuard(cur);
                lock.unlock();

                while (true) {
                    compare = cur.data.compareTo(data);
                    if (compare == 0) {
                        LockableNode rep = replacement(cur);

                        compare = par.data.compareTo(data);
                        if (compare > 0) par.left = rep;
                        else par.right = rep;

                        if (rep != null) {
                            acquireBarrier(rep);
                            rep.left = cur.left;
                            rep.right = cur.right;
                            releaseBarrier(rep);
                        }

                        releaseGuard(cur);
                        releaseGuard(par);
                        break;
                    } else {
                        releaseGuard(par);
                        par = cur;

                        compare = cur.data.compareTo(data);
                        if (compare > 0) cur = cur.left;
                        else cur = cur.right;
                    }

                    if (cur == null) return false;
                    else acquireGuard(cur);
                }
            } else {
                LockableNode rep = replacement(cur);
                root = rep;

                if (rep != null) {
                    acquireBarrier(rep);
                    rep.left = cur.left;
                    rep.right = cur.right;
                    releaseBarrier(rep);
                }

                releaseGuard(cur);
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
            acquireGuard(cur);

            while (cur.right != null) {
                if (par != sub) releaseGuard(par);
                par = cur;
                cur = cur.right;
                acquireGuard(cur);
            }

            acquireBarrierOverGuard(par);
            if (cur.left != null) acquireGuard(cur.left);
            if (par == sub) {
                par.left = cur.left;
                releaseBarrierOverGuard(par);
            } else {
                par.right = cur.left;
                releaseBarrier(par);
            }
            if (cur.left != null) releaseGuard(cur.left);
            releaseGuard(cur);
        } else if (sub.right != null) {
            cur = sub.right;
            acquireGuard(cur);

            while (cur.left != null) {
                if (par != sub) par.unlock();
                par = cur;
                cur = cur.left;
                acquireGuard(cur);
            }

            acquireBarrierOverGuard(par);
            if (cur.right != null) acquireGuard(cur.right);
            if (par == sub) {
                par.right = cur.right;
                releaseBarrierOverGuard(par);
            } else {
                par.left = cur.right;
                releaseGuard(par);
            }
            if (cur.right != null) releaseGuard(cur.right);
            releaseGuard(cur);
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
            acquireGuard(cur);
            lock.unlock();

            while (true) {
                int compare = cur.data.compareTo(data);
                if (compare == 0) {
                    releaseGuard(cur);
                    return true;
                } else {
                    LockableNode next = compare > 0 ? cur.left : cur.right;
                    if (next == null) {
                        releaseGuard(cur);
                        return false;
                    }
                    acquireGuard(next);
                    releaseGuard(cur);
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
        if (root != null) {
            acquireGuard(root);
            lock.unlock();
            preOrderHelper(root, f);
            return;
        }
        lock.unlock();
    }

    private void preOrderHelper(LockableNode node, final Consumer<Node<T>> f) {
        if (node == null) return;
        acquireGuard(node);
        f.accept(node);
        releaseGuard(node);
        preOrderHelper(node.left, f);
        preOrderHelper(node.right, f);
    }

    @Override
    public void inOrderTraversal(final Consumer<Node<T>> f) {
        lock.lock();
        if (root != null) {
            acquireGuard(root);
            lock.unlock();
            inOrderHelper(root, f);
            return;
        }
        lock.unlock();
    }

    private void inOrderHelper(LockableNode node, final Consumer<Node<T>> f) {
        if (node == null) return;
        inOrderHelper(node.left, f);
        acquireGuard(node);
        f.accept(node);
        releaseGuard(node);
        inOrderHelper(node.right, f);
    }
}
