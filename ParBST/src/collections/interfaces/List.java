package collections.interfaces;

public interface List<T> {
    class Node<F> {
        public F item;
        public Node<F> front;
        public Node<F> next;

        public Node(F item) {
            this.item = item;
            front = next = null;
        }

        public Node<F> makeNext(F item) {
            Node<F> next = new Node<>(item);
            next.front = this;
            this.next = next;
            return next;
        }

        public Node<F> makeFront(F item) {
            Node<F> front = new Node<>(item);
            front.next = this;
            this.front = front;
            return next;
        }
    }

    void add(int index, T item);
    boolean add(T item);
    boolean addFirst(T item);

    T get(int index);
    T getFirst();
    T getLast();

    int indexOf(T item);
    T remove(int index);
    boolean remove(T item);

    int size();
    Object[] toArray();
}
