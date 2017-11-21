package collections;

import collections.interfaces.List;

public class LinkedList<T> implements List<T> {
    private Node<T> head;
    private Node<T> last;
    private int counter;

    public LinkedList() {
        head = null;
        counter = 0;
    }

    @Override
    public void add(int index, T item) {
        Node<T> node = getNode(index);
        Node<T> next = node.next;
        node.makeNext(item).next = next;
        counter += 1;
    }

    @Override
    public boolean add(T item) {
        if (head == null) {
            last = head = new Node<>(item);
            counter += 1;
            return true;
        }

        last = last.makeNext(item);
        counter += 1;
        return true;
    }

    @Override
    public boolean addFirst(T item) {
        if (head == null) return false;
        head = head.makeFront(item);
        counter += 1;
        return true;
    }

    @Override
    public T get(int index) {
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
        while (node != last) {
            if (node.item == item) {
                removeNode(node);
                return true;
            }
            node = node.next;
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
        while (node.item != element) {
            node = node.next;
            ++index;
        }
        return index;
    }

    private T removeNode(Node<T> node) {
        if (node.front != null)
            node.front.next = node.next;
        if (node.next != null)
            node.next.front = node.front;
        counter -= 1;
        return node.item;
    }
}
