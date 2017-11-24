package collections.interfaces;

public interface List<T> extends Iterable<T> {
    class Iterator<F> implements java.util.Iterator<F> {
        Node next;

        public Iterator(Node next) {
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public F next() {
            next = next.next;
            return (F) next.item;
        }
    }

    class Node<F> {
        public F item;
        public Node<F> front;
        public Node<F> next;

        public Node(F item) {
            this.item = item;
            front = next = null;
        }

        public void remove() {
            if (this.front != null) front.next = next;
            if (this.next != null) next.front = front;
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
            return front;
        }
    }

    boolean add(int index, T item);
    boolean add(T item);
    boolean addFirst(T item);
    Node<T> addNode(T item);

    T get(int index);
    T getFirst();
    T getLast();

    int indexOf(T item);
    T remove(int index);
    boolean remove(T item);

    int size();
    Object[] toArray();

    Iterator iterator();
}
