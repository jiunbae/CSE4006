package collections.interfaces;

public interface List<T> extends Iterable<T> {
    class Iterator<F> implements java.util.Iterator<F> {
        Node<F> next;

        public Iterator(Node<F> next) {
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return next.next != null;
        }

        @Override
        public F next() {
            F value = next.item;
            next = next.next;
            return value;
        }
    }

    class Node<F> {
        public F item;
        public Node<F> front;
        public Node<F> next;
        List<F> parent;

        public Node(F item) {
            this.item = item;
            front = next = null;
        }

        public Node(F item, List<F> parent) {
            this.item = item;
            this.parent = parent;
            front = next = null;
        }

        public void remove() {
            parent.removeNode(this);
        }

        public Node<F> makeNext(F item) {
            Node<F> child = new Node<>(item, parent);
            if (next != null) {
                next.front = child;
                child.next = next;
            }
            child.front = this;
            next = child;
            return child;
        }

        public Node<F> makeFront(F item) {
            Node<F> child = new Node<>(item, parent);
            if (front != null) {
                front.next = child;
                child.front = front;
            }
            child.next = this;
            this.front = child;
            return child;
        }
    }

    boolean add(int index, T item);
    boolean add(T item);
    boolean addFirst(T item);
    Node<T> addNode(T item);

    T remove(int index);
    boolean remove(T item);
    <N extends Node<T>> T removeNode(N node);

    T get(int index);
    T getFirst();
    T getLast();
    boolean contains(T item);
    int indexOf(T item);

    int size();
    Object[] toArray();

    Iterator iterator();
}
