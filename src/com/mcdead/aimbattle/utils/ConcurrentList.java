package com.mcdead.aimbattle.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ConcurrentList<T extends Copyable> {
    private ConcurrentListItem<T> m_root;
    private ConcurrentListItem<T> m_tail;
    private AtomicInteger m_size;

    public ConcurrentList() {
        m_root = new ConcurrentListItem<T>(null); // init. obj.
        m_tail = m_root;

        m_size = new AtomicInteger(0);
    }

//    public static void main(String[] argv) {
//        ConcurrentList<Integer> someList = new ConcurrentList<>();
//
//        new Thread(() -> {
//            someList.addItem(4);
//            someList.addItem(5);
//            someList.addItem(6);
//            someList.addItem(7);
//            try {Thread.sleep(2000);} catch (Throwable e) {}
//            someList.addItem(8);
//            someList.addItem(9);
//            someList.addItem(10);
//            someList.addItem(11);
//            someList.addItem(12);
//        }).start();
//
//        someList.forEach((item) -> {
//            System.out.println(item);
//
//            return item;
//        });
//
//        someList.addItem(1);
//        someList.addItem(2);
//        someList.addItem(3);
//
//        someList.removeOneIf((val) -> {
//            return val == 2;
//        });
//
//        try {Thread.sleep(2000);} catch (Throwable e) {}
//
//        someList.forEach((item) -> {
//            System.out.println(item);
//
//            return item;
//        });
//
//        someList.removeOneIf((val) -> {
//            return val == 15;
//        });
//    }

    public void addItem(T newItemValue) {
        T valueCopy = (T) newItemValue.copy();

        ConcurrentListItem<T> newItem = new ConcurrentListItem<>(valueCopy);

        m_root.m_lock.lock();
        m_tail.m_lock.lock();

        if (m_root == m_tail) {
            m_root.m_lock.unlock();

            m_root.m_next = newItem;

            m_tail.m_lock.unlock();

            m_tail = m_root.m_next;

        } else {
            m_root.m_lock.unlock();

            m_tail.m_next = newItem;

            m_tail.m_lock.unlock();

            m_tail = m_tail.m_next;
        }

        m_size.incrementAndGet();
    }

    public boolean removeOneIf(Predicate<T> predicate) {
        if (predicate == null) return false;

        m_root.m_lock.lock();

        ConcurrentListItem<T> curItem = m_root;

        while (curItem.m_next != null) {
            curItem.m_next.m_lock.lock();

            if (predicate.test(curItem.m_next.m_value)) {
                curItem.m_next.m_lock.unlock();

                if (curItem.m_next.m_next != null) {
                    curItem.m_next = curItem.m_next.m_next;

                } else {
                    curItem.m_next = null;
                    m_tail = curItem;
                }

                curItem.m_lock.unlock();

                m_size.decrementAndGet();

                return true;
            }

            curItem.m_lock.unlock();
            curItem = curItem.m_next;
        }

        curItem.m_lock.unlock();

        return false;
    }

    public T getValueIf(Predicate<T> predicate) {
        m_root.m_lock.lock();

        ConcurrentListItem<T> curItem = m_root;

        while (curItem.m_next != null) {
            curItem.m_next.m_lock.lock();
            curItem.m_lock.unlock();
            curItem = curItem.m_next;

            if (predicate.test(curItem.m_value)) {
                T valueCopy = (T) curItem.m_value.copy();

                curItem.m_lock.unlock();

                return valueCopy;
            }
        }

        curItem.m_lock.unlock();

        return null;
    }

    public void forEach(UnaryOperator<T> lambda) {
        m_root.m_lock.lock();

        ConcurrentListItem<T> curItem = m_root;

        while (curItem.m_next != null) {
            curItem.m_next.m_lock.lock();
            curItem.m_lock.unlock();
            curItem = curItem.m_next;

            curItem.m_value = lambda.apply(curItem.m_value);
        }

        curItem.m_lock.unlock();
    }

    public boolean forFirstIf(Predicate<T> predicate, UnaryOperator<T> lambda) {
        m_root.m_lock.lock();

        ConcurrentListItem<T> curItem = m_root;

        while (curItem.m_next != null) {
            curItem.m_next.m_lock.lock();
            curItem.m_lock.unlock();
            curItem = curItem.m_next;

            if (predicate.test(curItem.m_value)) {
                curItem.m_value = lambda.apply(curItem.m_value);

                curItem.m_lock.unlock();

                return true;
            }
        }

        curItem.m_lock.unlock();

        return false;
    }

    public int getSize() {
        return m_size.get();
    }

    private class ConcurrentListItem<T> {
        private T m_value;
        private ConcurrentListItem<T> m_next;
        private ReentrantLock m_lock;

        public ConcurrentListItem(final T value) {
            m_value = value;
            m_next = null;
            m_lock = new ReentrantLock();
        }
    }
}
