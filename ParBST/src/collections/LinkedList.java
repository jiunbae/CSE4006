package collections;

import collections.interfaces.List;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class LinkedList<T> implements List<T> {
    private Node<T> head;
    private Node<T> last;
    private int counter;

    public LinkedList() {
        head = new Node<>(null, this);
        last = new Node<>(null, this);
        head.next = last;
        last.front = head;
        counter = 0;
    }

    @Override
    public boolean add(int index, T item) {
        try {
            Node<T> node = getNode(index);
            node.makeFront(item);
            counter += 1;
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean add(T item) {
        return addNode(item) != null;
    }

    @Override
    public boolean addFirst(T item) {
        head.makeNext(item);
        counter += 1;
        return true;
    }

    @Override
    public Node<T> addNode(T item) {
        counter += 1;
        return last.makeFront(item);
    }

    @Override
    public T get(int index) {
        try {
            Node<T> node = getNode(index);
            return node.item;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public T getFirst() {
        if (head.next == null) return null;
        return head.next.item;
    }

    @Override
    public T getLast() {
        if (last.front == null) return null;
        return last.front.item;
    }

    @Override
    public int indexOf(T item) {
        return getIndex(item);
    }

    @Override
    public T remove(int index) {
        return removeNode(getNode(index));
    }

    @Override
    public boolean remove(T item) {
        for (Node<T> node = head.next; node != last; node = node.next) {
            if (node.item.equals(item)) {
                removeNode(node);
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return counter;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[counter];
        int i = 0;
        for (Node<T> node = head.next; node != last; node = node.next)
            result[i++] = node.item;
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>(head.next);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (T t : this) action.accept(t);
    }

    @Override
    public Spliterator<T> spliterator() {
        return null;
    }

    private Node<T> getNode(int index) {
        for (Node<T> node = head.next; node != last; node = node.next) {
            if (index-- == 0)
                return node;
        }
        return null;
    }

    private int getIndex(T element) {
        int index = 0;
        for (Node<T> node = head.next; node != last; node = node.next) {
            if (node.item.equals(element))
                return index;
            index++;
        }
        return index;
    }

    @Override
    public T removeNode(Node<T> node) {
        if (node.front != null) node.front.next = node.next;
        if (node.next != null) node.next.front = node.front;
        counter -= 1;
        return node.item;
    }
}
