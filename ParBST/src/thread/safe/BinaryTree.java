package thread.safe;

import java.util.function.Consumer;
import java.util.concurrent.locks.ReentrantLock;

public class BinaryTree<T extends Comparable<? super T>> {
    ReentrantLock lock;
    Node<T> root;

    public BinaryTree() {
        lock = new ReentrantLock();
        root = null;
    }

    public void insert(T data) {
        lock.lock();
        if (root == null) {
            root = new Node<>(data);
            lock.unlock();
        } else {
            root.lock();
            lock.unlock();
            insert(root, data);
        }
    }

    private Node<T> insert(Node<T> node, T data) {
        if (node == null) return new Node<>(data);

        int compare = node.data.compareTo(data);
        if (compare != 0) {

            if (compare < 0) {
                if (node.left != null) node.left.lock();
                node.unlock();
                node.left = insert(node.left, data);
            } else {
                if (node.right != null) node.right.lock();
                node.unlock();
                node.right = insert(node.right, data);
            }
        } else node.unlock();

        return node;
    }

    public boolean delete(T data) {
        lock.lock();
        try {
            root.lock();
            lock.unlock();
            root = delete(root, data);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Node<T> delete(Node<T> node, T data) {
        if (node == null) {
            throw new RuntimeException("No item to delete");
        }

        int compare = node.data.compareTo(data);
        if (compare < 0) {
            if (node.left != null) node.left.lock();
            node.unlock();
            node.left = delete(node.left, data);
        } else if (compare > 0) {
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
                node.left = delete(node.left, node.data) ;
            }
        }
        return node;
    }

    private T retrieveData(Node<T> node) {
        while (node.right != null) node = node.right;
        return node.data;
    }

    public void preOrderTraversal(final Consumer<Node> f) {
        lock.lock();
        preOrderHelper(root, f);
        lock.unlock();
    }

    private void preOrderHelper(Node r, final Consumer<Node> f) {
        if (r == null) return;
        f.accept(r);
        preOrderHelper(r.left, f);
        preOrderHelper(r.right, f);
    }

    public void inOrderTraversal(final Consumer<Node> f) {
        lock.lock();
        inOrderHelper(root, f);
        lock.unlock();
    }

    private void inOrderHelper(Node r, final Consumer<Node> f) {
        if (r == null) return;
        inOrderHelper(r.left, f);
        f.accept(r);
        inOrderHelper(r.right, f);
    }
}
