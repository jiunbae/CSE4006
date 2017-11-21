package thread.safe;

import interfaces.Tree;

import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.concurrent.locks.ReentrantLock;

public class BinaryTree<T extends Comparable<? super T>> implements interfaces.Tree<T> {
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

    @Override
    public boolean insert(T data) {
        lock.lock();
        try {
            if (root == null) {
                root = new LockableNode(data);
                lock.unlock();
            } else {
                root.lock();
                lock.unlock();
                insert(root, data);
            }
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private LockableNode insert(LockableNode node, T data) {
        if (node == null) return new LockableNode(data);

        int compare = node.data.compareTo(data);
        if (compare != 0) {
            if (compare > 0) {
                if (node.left != null) node.left.lock();
                node.unlock();
                node.left = insert(node.left, data);
            } else {
                if (node.right != null) node.right.lock();
                node.unlock();
                node.right = insert(node.right, data);
            }
        } else {
            node.unlock();
            throw new RuntimeException("Item already exists");
        }

        return node;
    }

    @Override
    public boolean delete(T data) {
        lock.lock();
        try {
            if (root == null) throw new RuntimeException("No item to delete");
            delete(root, data);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private LockableNode delete(LockableNode node, T data) {
        if (node == null) {
            throw new RuntimeException("No item to delete");
        }

        int compare = node.data.compareTo(data);
        if (compare > 0) {
            if (node.left != null) node.left.lock();
            node.unlock();
            node.left = delete(node.left, data);
        } else if (compare < 0) {
            if (node.right != null) node.right.lock();
            node.unlock();
            node.right = delete(node.right, data);
        } else {
            if (node.left == null) return node.right;
            else if (node.right == null) return node.left;
            else {
                node.data = retrieveData(node.left);
                if (node.left != null) node.left.lock();
                node.unlock();
                node.left = delete(node.left, node.data);
            }
        }
        return node;
    }

    private T retrieveData(Node<T> node) {
        while (node.right != null) node = node.right;
        return node.data;
    }

    public boolean search(T data) {
        lock.lock();
        boolean result = search(root, data);
        lock.unlock();
        return result;
    }

    private boolean search(LockableNode node, T data) {
        if (node == null)
            return false;
        else {
            int compare = node.data.compareTo(data);
            if (compare > 0) return search(node.left, data);
            else if (compare < 0) return search(node.right, data);
            else return true;
        }
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
