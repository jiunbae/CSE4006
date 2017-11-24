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
        head = null;
        counter = 0;
    }

    @Override
    public boolean add(int index, T item) {
        if (head == null) {
            last = head = new Node<>(item);
            counter += 1;
            return true;
        }

        try {
            Node<T> node = getNode(index);
            Node<T> front = node.front;
            Node<T> child = node.makeFront(item);
            if (front != null) front.next = child;
            child.front = front;

            if (head.front != null) head = head.front;
            if (last.next != null) last = last.next;

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
        if (head == null) return false;
        head = head.makeFront(item);
        counter += 1;
        return true;
    }

    @Override
    public Node<T> addNode(T item) {
        if (head == null) {
            Node<T> child = new Node<>(item);
            head = last = child;
            counter += 1;
            return child;
        }

        counter += 1;
        return last = last.makeNext(item);
    }

    @Override
    public T get(int index) throws NullPointerException {
        return getNode(index).item;
    }

    @Override
    public T getFirst() {
        return head.item;
    }

    @Override
    public T getLast() {
        return last.item;
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
        Node<T> node = head;
        if (node.item.equals(item)) {
            removeNode(node);
            return true;
        } else {
            while (node.next != null) {
                if (node.item.equals(item)) {
                    removeNode(node);
                    return true;
                }
                node = node.next;
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
        for (Node<T> node = head; node != null; node = node.next)
            result[i++] = node.item;
        return result;
    }

    @Override
    public Iterator iterator() {
        return new Iterator(head);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (Iterator it = iterator(); it.hasNext();)
            action.accept((T) it.next());
    }

    @Override
    public Spliterator<T> spliterator() {
        return null;
    }

    private Node<T> getNode(int index) {
        try {
            Node<T> node = head;
            while (index-->0) node = node.next;
            return node;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private int getIndex(T element) {
        Node<T> node = head;
        int index = 0;
        while (!node.item.equals(element)) {
            node = node.next;
            ++index;
        }
        return index;
    }

    private T removeNode(Node<T> node) {
        if (node.front != null) node.front.next = node.next;
        else head = node.next;

        if (node.next != null) node.next.front = node.front;
        else last = node.front;
        counter -= 1;
        return node.item;
    }
}
