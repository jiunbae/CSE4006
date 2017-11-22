package collections;

import collections.interfaces.Tree;

import java.util.function.Consumer;

public class BinaryTree<T extends Comparable<? super T>> implements collections.interfaces.Tree<T> {
    private Node<T> root;
    private int size;

    public BinaryTree() {
        root = null;
        size = 0;
    }

    @Override
    public boolean insert(T data) {
        try {
            root = insert(root, data);
            size += 1;
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Tree.Node<T> insert(Tree.Node<T> p, T data) throws RuntimeException {
        if (p == null) return new Node<>(data);

        int compare = p.data.compareTo(data);
        if (compare > 0)
            p.left = insert(p.left, data);
        else if (compare < 0)
            p.right = insert(p.right, data);
        else
            throw new RuntimeException("Item already exists");

        return p;
    }

    @Override
    public boolean search(T data) {
        return search(root, data);
    }

    private boolean search(Node<T> node, T data) {
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
    public boolean delete(T data) {
        try {
            root = delete(root, data);
            size -= 1;
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Node<T> delete(Node<T> node, T data) {
        if (node == null) {
            throw new RuntimeException("cannot delete.");

        } else {
            int compare = node.data.compareTo(data);
            if (compare > 0) {
                node.left = delete (node.left, data);
            } else if (compare < 0) {
                node.right = delete (node.right, data);
            } else {
                if (node.left == null) return node.right;
                else if (node.right == null) return node.left;
                else {
                    node.data = retrieveData(node.left);
                    node.left =  delete(node.left, node.data) ;
                }
            }
        }

        return node;
    }

    private T retrieveData(Node<T> p) {
        while (p.right != null) {
            p = p.right;
        }
        return p.data;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void preOrderTraversal(final Consumer<Node<T>> f) {
        preOrderHelper(root, f);
    }

    private void preOrderHelper(Tree.Node<T> node, final Consumer<Node<T>> f) {
        if (node == null) return;
        f.accept(node);
        preOrderHelper(node.left, f);
        preOrderHelper(node.right, f);
    }

    @Override
    public void inOrderTraversal(final Consumer<Node<T>> f) {
        inOrderHelper(root, f);
    }

    private void inOrderHelper(Tree.Node<T> node, final Consumer<Node<T>> f) {
        if (node == null) return;
        inOrderHelper(node.left, f);
        f.accept(node);
        inOrderHelper(node.right, f);
    }
}
