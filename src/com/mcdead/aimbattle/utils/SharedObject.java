package com.mcdead.aimbattle.utils;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class SharedObject<T extends Copyable> {
    private T m_value;
    private ReentrantLock m_lock;

    public SharedObject(final T value) {
        m_value = (T) value.copy();
        m_lock = new ReentrantLock();
    }

    public T getValue() {
        m_lock.lock();

        T valueCopy = (T) m_value.copy();

        m_lock.unlock();

        return valueCopy;
    }

    public boolean setValue(final T newValue) {
        if (newValue == null) return false;

        m_lock.lock();

        m_value = (T) newValue.copy();

        m_lock.unlock();

        return true;
    }

    public boolean modifyValue(final UnaryOperator<T> lambda) {
        if (lambda == null) return false;

        m_lock.lock();

        m_value = lambda.apply(m_value);

        m_lock.unlock();

        return true;
    }
}
