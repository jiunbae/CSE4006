package cse4006.utility;

public class Queue<T> {
    //private static final int DEFAULT_SIZE = 16; // use *static* cause using in constructor.
    public int cap = 0;
    private int front = 0;
    private int rear = 0;

    private T[] elements = null;

    public Queue() {
        this(16);                           //@Improving: use *static* keyword if there is no constraint
    }

    public Queue(int cap) {
        this.cap = cap + 1;
        elements = (T[])new Object[cap];
    }

    public Queue(T[] array) {
        this(array.length);

        for (T e : array) {
            add(e);
        }
    }

    public final int getSize() {
        if(rear > front)
            return rear - front;
        return cap - front + rear;
    }

    public final boolean isEmpty() {
        return rear == front;
    }

    public final boolean isFull() {
        return rear - front == -1 || rear - front == (cap - 1);
    }

    public void adjust(int size) {
        T[] latest = (T[]) new Object[size];

        int cnt = 0;
        while (!isEmpty()) {
            try {
                Object element = elements[front];
                elements[front] = null;
                front = (++front) % cap;
                latest[cnt++] = (T) element;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        front = 0;
        rear = cnt;
        cap = size;
        elements = latest;
    }

    public void add(T value) {
        if (isFull())
            adjust(cap * 2);

        elements[rear] = value;
        rear = (++rear) % cap;
    }

    public T pop() {
        if (isEmpty()) {
            return null;
        } else {
            if (getSize() < cap / 2) {
                adjust(cap / 2);
            }
            Object element = elements[front];
            elements[front] = null;
            front = (++front) % cap;
            return (T) element;
        }
    }

}
