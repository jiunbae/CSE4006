package collections.concurrent;

import collections.interfaces.Tree;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * An implementation of {@link Tree}, by fine-grained method.
 * Concurrent additional implementation of {@link collections.LinkedList}.
 *
 * Fine-grained locking with insert, delete, search methods.
 *
 * See some test on BinaryTreeTest.
 *
 * @param <T>
 */
public class BinaryTree<T extends Comparable<? super T>> implements Tree<T> {
    /**
     * Node which lock enabled extends Tree.Node
     */
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

    private ReentrantLock lock;
    private LockableNode root;

    public BinaryTree() {
        lock = new ReentrantLock();
        root = null;
    }

    /**
     * First of all, hold global lock(tree master lock) and test root is null.
     * If root is null assign new node to root and release global lock.
     * Else, iterate node from lock to target which is null switching left or right as compare.
     * While iterating try to hold next node lock and release current node lock and switch current to next.
     *
     * @param data to insert
     * @return success
     */
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
        return true;
    }

    /**
     * First of all, hold global lock(tree master lock) and test root is null.
     * If root is null release global lock and return false
     * Else, iterate node from lock to target which is null switching left or right as compare.
     * Unlike {@link #insert(Comparable)} is holding parent's node locking while tracing and delete.
     * Because deletion of node can change value of node while other thread comparing parent.
     *
     * @param data to delete
     * @return success
     */
    @Override
    public boolean delete(T data) {
        lock.lock();
        if (root == null) {
            lock.unlock();
            return false;
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
        return true;
    }

    /**
     * Find out replacement of node which to delete.
     *
     * @param sub root
     * @return rightmost of left or leftmost of right if exists
     */
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

    /**
     * Same as {@link #insert(Comparable)} or {@link #delete(Comparable)}.
     * But there is no critical section, just reading.
     *
     * @param data to search
     * @return success
     */
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
        if (node.right != null) {
            node.right.lock();
            inOrderHelper(node.right, f);
        }
    }
}
