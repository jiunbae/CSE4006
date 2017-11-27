package collections.interfaces;

import java.util.function.Consumer;

/**
 * Tree is a collection of {@link Tree.Node}
 * which can {@link #insert(Comparable)}, {@link #delete(Comparable)}, {@link #search(Comparable)} node.
 *
 * @param <T>
 */
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

    /**
     * @param data to insert
     * @return success
     */
    boolean insert(T data);

    /**
     * @param data to delete
     * @return success
     */
    boolean delete(T data);

    /**
     * @param data to search
     * @return success
     */
    boolean search(T data);

    /**
     * Traverse tree as pre-order applying f
     * @param f to apply
     */
    void preOrderTraversal(final Consumer<Node<T>> f);

    /**
     * Traverse tree as in-order applying f
     * @param f to apply
     */
    void inOrderTraversal(final Consumer<Node<T>> f);
}
