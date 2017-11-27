package collections.interfaces;

public interface List<T> extends Iterable<T> {
    /**
     * Iterator of {@link List}, @see java.util.Iterator
     * Call {@link List#iterator()} to get iterator
     *
     * @param <F>
     */
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

    /**
     * Node of {@link List}, must have item, front, next fields as public
     *
     * @param <F>
     */
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

    /**
     * Insert to last of list
     *
     * @param item to insert
     * @return success
     */
    boolean add(T item);

    /**
     * @param item to delete
     * @return success
     */
    boolean remove(T item);

    T get(int index);
    T getFirst();
    T getLast();

    /**
     * Assert item is contained
     *
     * @param item to assert
     * @return success
     */
    boolean contains(T item);

    /**
     * Get index of item in list
     *
     * @param item to find
     * @return index of item in list
     */
    int indexOf(T item);

    /**
     * @return list size
     */
    int size();

    /**
     * @return list as array
     */
    Object[] toArray();

    /**
     * Iterator {@link Iterator}
     * @return iterator of list from first
     */
    Iterator iterator();
}
