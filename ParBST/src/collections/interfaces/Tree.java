package collections.interfaces;

        import java.util.function.Consumer;

public interface Tree<T extends Comparable<? super T>> {
    class Node<F extends Comparable<? super F>> {
        public F data;
        public Node<F> left;
        public Node<F> right;

        public Node(F data) {
            this.data = data;
            left = right = null;
        }
    }

    boolean insert(T data);
    boolean delete(T data);
    boolean search(T data);
    int size();

    void preOrderTraversal(final Consumer<Node<T>> f);
    void inOrderTraversal(final Consumer<Node<T>> f);
}
