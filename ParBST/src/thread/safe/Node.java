package thread.safe;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node<T> {
    public T data;
    public Node<T> left;
    public Node<T> right;
    private Lock lock;

    public Node(T data) {
        this.data = data;
        left = right = null;
        lock = new ReentrantLock();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
