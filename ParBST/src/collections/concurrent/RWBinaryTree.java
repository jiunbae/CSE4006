package collections.concurrent;

import collections.interfaces.Tree;
import concurrent.locks.ReentrantReadWriteOrderedLock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class RWBinaryTree<T extends Comparable<? super T>> implements Tree<T> {
    public class LockableNode extends Tree.Node<T> {
        LockableNode left;
        LockableNode right;

        final ReadWriteLock locker;
        final ReentrantReadWriteOrderedLock.ReadLock readLock;
        final ReentrantReadWriteOrderedLock.WriteLock writeLock;


        public LockableNode(T data) {
            super(data);
            locker = new ReentrantReadWriteOrderedLock();
            readLock = (ReentrantReadWriteOrderedLock.ReadLock) locker.readLock();
            writeLock = (ReentrantReadWriteOrderedLock.WriteLock) locker.writeLock();
        }

        void lock() {
            System.out.println(String.format("thread %d, node %s, READ: request lock", Thread.currentThread().getId(), data));
            readLock.lock();
            System.out.println(String.format("thread %d, node %s, READ: acquire lock", Thread.currentThread().getId(), data));
        }

        void unlock() {
            System.out.println(String.format("thread %d, node %s, READ: request release", Thread.currentThread().getId(), data));
            readLock.unlock();
            System.out.println(String.format("thread %d, node %s, READ: accept release", Thread.currentThread().getId(), data));
        }

        /**
         * Simplifies read scope.
         * It is safer than calling {@link LockableNode#lock()} and {@link LockableNode#unlock()} directly.
         *
         * @param f Return value T, which applied
         * @return T which return from f
         */
        T read(final Function<LockableNode, T> f) {
            readLock.lock();
            try {
                return f.apply(this);
            } finally {
                readLock.unlock();
            }
        }

        /**
         * Critical section of {@link LockableNode}
         * In write action, must unlock read and wait for write lock.
         * If acquire write lock, then do action and release write lock.
         * Finally re-acquire reader locker for safety.
         *
         * @param f Return false raise dirty write when acquire write lock,
         *          must configure logic to re-try, else return true
         */
        boolean write(final Function<LockableNode, Boolean> f) {
            System.out.println(String.format("thread %d, node %s, WRITE: request read release for write", Thread.currentThread().getId(), data));
            readLock.unlock();
            System.out.println(String.format("thread %d, node %s, WRITE: accept read release for write", Thread.currentThread().getId(), data));
            System.out.println(String.format("thread %d, node %s, WRITE: request lock", Thread.currentThread().getId(), data));
            writeLock.lock();
            System.out.println(String.format("thread %d, node %s, WRITE: acquire lock", Thread.currentThread().getId(), data));
            try {
                System.out.println(String.format("thread %d, node %s, WRITE: write start", Thread.currentThread().getId(), data));
                return f.apply(this);
            } catch (RuntimeException e) {
                System.out.println("RE raised");
            } finally {
                System.out.println(String.format("thread %d, node %s, WRITE: write done", Thread.currentThread().getId(), data));
                System.out.println(String.format("thread %d, node %s, WRITE: lock downgrade", Thread.currentThread().getId(), data));
                writeLock.downgrade();
                System.out.println(String.format("thread %d, node %s, WRITE: lock downgrade done", Thread.currentThread().getId(), data));
            }
            return true;
        }
    }

    private final Lock lock;
    private LockableNode root;

    public RWBinaryTree() {
        lock = new ReentrantLock();
        root = null;
    }

    @Override
    public boolean insert(T data) {
        lock.lock();
        System.out.println(String.format("thread %d, insert %d", Thread.currentThread().getId(), data));
        if (root == null) {
            root = new LockableNode(data);
            lock.unlock();
        } else {
            LockableNode cur = root;
            cur.lock();
            lock.unlock();

            while (true) {
                int compare = cur.data.compareTo(data);
                if (compare == 0) {
                    cur.unlock();
                    return false;
                }

                LockableNode next = compare > 0 ? cur.left : cur.right;
                if (next == null) {
                    System.out.println(String.format("thread %d, throw into critical section at %d", Thread.currentThread().getId(), cur.data));
                    if (cur.write((c) -> {
                        if (compare > 0) {
                            if (c.left != null) {
                                System.out.println(String.format("thread %d, detect dirty write in cs %d", Thread.currentThread().getId(), c.data));
                                return false;
                            }
                            else {
                                System.out.println(String.format("thread %d, write is clear and start writing", Thread.currentThread().getId(), c.data));
                                c.left = new LockableNode(data);
                            }
                        }
                        else {
                            if (c.right != null) {
                                System.out.println(String.format("thread %d, detect dirty write in cs %d", Thread.currentThread().getId(), c.data));
                                return false;
                            }
                            else {
                                System.out.println(String.format("thread %d, write is clear and start writing", Thread.currentThread().getId(), c.data));
                                c.right = new LockableNode(data);
                            }
                        }
                        return true;
                    })) {
                        System.out.println(String.format("thread %d, throw out from critical with success", Thread.currentThread().getId(), cur.data));
                        cur.unlock();
                        break;
                    }
                    System.out.println(String.format("thread %d, throw out from critical section with failed", Thread.currentThread().getId(), cur.data));
                } else {
                    next.lock();
                    cur.unlock();
                    cur = next;
                }
            }
        }
        return true;
    }
//
//    @Override
//    public boolean delete(T data) {
//        lock.lock();
//        System.out.println(String.format("thread %d, delete %d", Thread.currentThread().getId(), data));
//        if (root == null) {
//            lock.unlock();
//        } else {
//            LockableNode cur = root;
//            LockableNode par = null;
//            cur.lock();
//
//            int compare = cur.data.compareTo(data);
//            // no exception here, cuz holding global lock
//            if (compare == 0) {
//                if (!cur.write((c) -> {
//                    LockableNode itPar = c;
//                    LockableNode itCur;
//                    if (c.left != null) {
//                        itCur = c.left;
//                        while (itCur.right != null) {
//                            itCur.right.lock();
//                            if (itPar != c) itPar.unlock();
//                            itPar = itCur;
//                            itCur = itCur.right;
//                        }
//
//                        if (itPar == c) c.left = itCur.left;
//                        else {
//                            LockableNode finalItCur = itCur;
//                            itPar.write((p) -> {
//                                if (p.right == null) {
//                                    System.out.println(String.format("thread %d, node %s, DEL: find replacement error %d", Thread.currentThread().getId(), finalItCur.data, p.data));
//                                } else
//                                    p.right = finalItCur.left;
//                                return true;
//                            });
//                        }
//                        c.data = itCur.data;
//                        if (itPar != c) itPar.unlock();
//                        itCur.unlock();
//                    } else if (c.right != null) {
//                        itCur = c.right;
//                        while (itCur.left != null) {
//                            itCur.left.lock();
//                            if (itPar != c) itPar.unlock();
//                            itPar = itCur;
//                            itCur = itCur.left;
//                        }
//
//                        if (itPar == c) c.right = itCur.right;
//                        else {
//                            LockableNode finalItCur = itCur;
//                            itPar.write((p) -> {
//                                if (p.left == null) {
//                                    System.out.println(String.format("thread %d, node %s, DEL: find replacement error %d", Thread.currentThread().getId(), finalItCur.data, p.data));
//                                } else
//                                    p.left = finalItCur.right;
//                                return true;
//                            });
//                        }
//                        c.data = itCur.data;
//                        if (itPar != c) itPar.unlock();
//                        itCur.unlock();
//                    } else {
//                        return false;
//                    }
//                    return true;
//                })) {
//                    root = null;
//                }
//                cur.unlock();
//                lock.unlock();
//            } else {
//                lock.unlock();
//                while (true) {
//                    if (par != null) par.unlock();
//                    par = cur;
//                    cur = compare > 0 ? cur.left : cur.right;
//                    if (cur == null) {
//                        par.unlock();
//                        return false;
//                    }
//                    cur.lock();
//                    compare = cur.data.compareTo(data);
//
//                    if (compare == 0) {
//                        LockableNode finalCur = cur;
//                        if (par.write((p) -> {
//                            if (finalCur.data.compareTo(data) == 0) {
//                                return finalCur.write((c) -> {
//                                    return true;
//                                });
//                            } else {
//                                System.out.println(String.format("thread %d, node %s, DEL: assertion failed %s", Thread.currentThread().getId(), finalCur.data, data));
//                                return false;
//                            }
//                        })) {
//                            par.unlock();
//                            cur.unlock();
//                            break;
//                        } else {
//                            System.out.println(String.format("thread %d, node %s, DEL: critical assertion failed %s", Thread.currentThread().getId(), finalCur.data, data));
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println(String.format("thread %d, delete %d done.", Thread.currentThread().getId(), data));
//        return true;
//    }

    @Override
    public boolean delete(T data) {
        lock.lock();
        if (root == null) {
            lock.unlock();
        } else {
            LockableNode cur = root;
            LockableNode par;
            cur.writeLock.lock();

            int compare = cur.data.compareTo(data);
            if (compare != 0) {
                par = cur;
                cur = compare > 0 ? cur.left : cur.right;
                cur.writeLock.lock();
                lock.unlock();

                while (true) {
                    compare = cur.data.compareTo(data);
                    if (compare == 0) {
                        LockableNode rep = replacement(cur);

                        compare = par.data.compareTo(data);
                        if (compare > 0) par.left = rep;
                        else par.right = rep;

                        if (rep != null) {
                            rep.left = cur.left;
                            rep.right = cur.right;
                        }

                        cur.writeLock.unlock();
                        par.writeLock.unlock();
                        break;
                    } else {
                        par.writeLock.unlock();
                        par = cur;

                        compare = cur.data.compareTo(data);
                        if (compare > 0) cur = cur.left;
                        else cur = cur.right;
                    }

                    if (cur == null) {
                        par.writeLock.unlock();
                        return false;
                    }
                    else cur.writeLock.lock();
                }
            } else {
                LockableNode rep = replacement(cur);
                root = rep;

                if (rep != null) {
                    rep.left = cur.left;
                    rep.right = cur.right;
                }

                cur.writeLock.unlock();
                lock.unlock();
            }

        }
        return true;
    }

    private LockableNode replacement(LockableNode sub) {
        LockableNode cur;
        LockableNode par = sub;

        if (sub.left != null) {
            cur = sub.left;
            cur.writeLock.lock();

            while (cur.right != null) {
                if (par != sub) par.writeLock.unlock();
                par = cur;
                cur = cur.right;
                cur.writeLock.lock();
            }

            if (cur.left != null) cur.left.writeLock.lock();
            if (par == sub) par.left = cur.left;
            else {
                par.right = cur.left;
                par.unlock();
            }
            if (cur.left != null) cur.left.writeLock.unlock();

            cur.writeLock.unlock();
        } else if (sub.right != null) {
            cur = sub.right;
            cur.writeLock.lock();

            while (cur.left != null) {
                if (par != sub) par.writeLock.unlock();
                par = cur;
                cur = cur.left;
                cur.writeLock.lock();
            }

            if (cur.right != null) cur.right.writeLock.lock();
            if (par == sub) par.right = cur.right;
            else {
                par.left = cur.right;
                par.writeLock.unlock();
            }
            if (cur.right != null) cur.right.writeLock.unlock();

            cur.writeLock.unlock();
        } else return null;
        return cur;
    }

    @Override
    public boolean search(T data) {
        lock.lock();
        if (root == null) {
            lock.unlock();
            return false;
        } else {
            LockableNode cur = root;
            cur.lock();
            lock.unlock();

            while (true) {
                int compare = cur.data.compareTo(data);
                if (compare == 0) {
                    cur.unlock();
                    return true;
                } else {
                    LockableNode next = compare > 0 ? cur.left : cur.right;
                    if (next == null) {
                        cur.unlock();
                        return false;
                    }

                    next.lock();
                    cur.unlock();
                    cur = next;
                }
            }
        }
    }

    @Override
    public void preOrderTraversal(final Consumer<Node<T>> f) {
        lock.lock();
        if (root == null) {
            lock.unlock();
            return;
        }
        root.lock();
        preOrderHelper(root, f);
        lock.unlock();
    }

    private void preOrderHelper(LockableNode node, final Consumer<Node<T>> f) {
        f.accept(node);
        node.unlock();

        if (node.left != null) {
            node.left.lock();
            preOrderHelper(node.left, f);
        }

        if (node.right != null) {
            node.right.lock();
            preOrderHelper(node.right, f);
        }
    }

    @Override
    public void inOrderTraversal(final Consumer<Node<T>> f) {
        lock.lock();
        if (root == null) {
            lock.unlock();
            return;
        }
        root.lock();
        inOrderHelper(root, f);
        lock.unlock();
    }

    private void inOrderHelper(LockableNode node, final Consumer<Node<T>> f) {
        if (node.left != null) {
            node.left.lock();
            inOrderHelper(node.left, f);
        }

        f.accept(node);
        node.unlock();

        if (node.right != null) {
            node.right.lock();
            inOrderHelper(node.right, f);
        }
    }
}
