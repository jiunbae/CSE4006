package collections.interfaces;

public interface List<T> {
    class Node<F> {
        public F data;
        public Node<F> left;
        public Node<F> right;

        public Node(F data) {
            this.data = data;
            left = right = null;
        }
    }

    void add(int index, T element);
    boolean add(T element);
    boolean addFirst(T element);

    T get(int index);
    T getFirst();
    T getLast();

    int indexOf(T element);
    T remove(int index);
    boolean remove(T element);

    int size();
    T[] toArray();
}
