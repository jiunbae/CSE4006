package collections.concurrent.lockfree;

import collections.interfaces.List;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LinkedList<T extends Comparable<? super T>> implements List<T> {
    private Node<T> head;
    private Node<T> tail;
    private int counter;

    class Node<F extends Comparable<? super F>> extends List.Node<F> {
        F item;
        AtomicMarkableReference<Node<F>> front;
        AtomicMarkableReference<Node<F>> next;

        public Node(F item) {
            super(item);
            this.item = item;
            front = new AtomicMarkableReference<>(null, false);
            next = new AtomicMarkableReference<>(null, false);
        }
    }

    class Window<F extends Comparable<? super F>> {
        Node<F> pre, cur;
        Window (Node<F> pre, Node<F> cur) {
            this.pre = pre;
            this.cur = cur;
        }
    }

    public Window<T> find(Node<T> head, T item) {
        Node<T> pre;
        Node<T> cur;
        Node<T> suc;
        boolean[] marked = {false};
        boolean snip;
        retry: while (true) {
            pre = head;
            cur = pre.next.getReference();
            while (true) {
                suc = cur.next.get(marked);
                while (marked[0]) {
                    snip = pre.next.compareAndSet(cur, suc, false, false);
                    if (!snip) continue retry;
                    cur = suc;
                    suc = cur.next.get(marked);
                }

                if (cur.item.compareTo(item) < 0)
                    return new Window<>(pre, cur);
                pre = cur;
                cur = suc;
            }
        }
    }

    @Override
    public boolean add(int index, T item) {
        return false;
    }

    @Override
    public boolean add(T item) {
        return false;
    }

    @Override
    public boolean addFirst(T item) {
        return false;
    }

    @Override
    public Node<T> addNode(T item) {
        return null;
    }

    @Override
    public T get(int index) {
        return null;
    }

    @Override
    public T getFirst() {
        return null;
    }

    @Override
    public T getLast() {
        return null;
    }

    @Override
    public int indexOf(T item) {
        return 0;
    }

    @Override
    public T remove(int index) {
        return null;
    }

    @Override
    public boolean remove(T item) {
        return false;
    }

    @Override
    public <N extends List.Node<T>> T removeNode(N node) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public Iterator iterator() {
        return null;
    }
}
