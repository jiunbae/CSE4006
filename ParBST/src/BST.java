import java.util.function.Consumer;
import static utility.Number.Operator;
import static utility.Number.eval;

public class BST<T extends Number> {
    public class Node {
        private T data;
        private Node left;
        private Node right;

        Node(T data, Node l, Node r) {
            left = l;
            right = r;
            this.data = data;
        }

        Node(T data) {
            this(data, null, null);
        }

        public String toString() {
            return "" + data;
        }
    }

    private Node root;
    private int mSize;

    public BST() {
        root = null;
        mSize = 0;
    }

    public final int size() {
        return mSize;
    }

    public void insert(T data) {
        root = insert(root, data);
        mSize += 1;
    }

    private Node insert(Node p, T toInsert) {
        if (p == null) return new Node(toInsert);
        if (toInsert == p.data) return p;

        if (eval(toInsert, p.data, Operator.LT))
            p.left = insert(p.left, toInsert);
        else
            p.right = insert(p.right, toInsert);

        return p;
    }

    public T findMin() {
        if (root == null) throw new RuntimeException("cannot findMin.");

        Node n = root;
        while (n.left != null) {
            n = n.left;
        }

        return n.data;
    }

    public boolean search(T toSearch) {
        return search(root, toSearch);
    }

    private boolean search(Node p, T toSearch) {
        if (p == null)
            return false;
        else if (toSearch == p.data)
            return true;
        else if (eval(toSearch, p.data, Operator.LT))
            return search(p.left, toSearch);
        else
            return search(p.right, toSearch);
    }

    public boolean delete(T toDelete) {
        try {
            root = delete(root, toDelete);
            mSize -= 1;
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Node delete(Node p, T toDelete) {
        if (p == null) {
            throw new RuntimeException("cannot delete.");
        } else if (eval(toDelete, p.data, Operator.LT)) {
            p.left = delete (p.left, toDelete);
        } else if (eval(toDelete, p.data, Operator.GT)) {
            p.right = delete (p.right, toDelete);
        } else {
            if (p.left == null) { return p.right; }
            else if (p.right == null) { return p.left;}
            else {
                // get data from the rightmost node in the left subtree
                p.data = retrieveData(p.left);
                // delete the rightmost node in the left subtree
                p.left =  delete(p.left, p.data) ;
            }
        }
        return p;
    }

    private T retrieveData(Node p) {
        while (p.right != null) {
            p = p.right;
        }
        return p.data;
    }

    public void preOrderTraversal(final Consumer<Node> f) {
        preOrderHelper(root, f);
    }

    private void preOrderHelper(Node r, final Consumer<Node> f) {
        if (r == null) return;
        f.accept(r);
        preOrderHelper(r.left, f);
        preOrderHelper(r.right, f);
    }

    public void inOrderTraversal(final Consumer<Node> f) {
        inOrderHelper(root, f);
    }

    private void inOrderHelper(Node r, final Consumer<Node> f) {
        if (r == null) return;
        inOrderHelper(r.left, f);
        f.accept(r);
        inOrderHelper(r.right, f);
    }
}
