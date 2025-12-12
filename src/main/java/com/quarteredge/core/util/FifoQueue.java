package com.quarteredge.core.util;

import java.util.ArrayDeque;

public class FifoQueue<E> {
    private final ArrayDeque<E> queue;
    private final int capacity;

    public FifoQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayDeque<>(capacity);
    }

    public void add(E e) {
        if (queue.size() == capacity) {
            queue.removeFirst();
        }
        queue.addLast(e);
    }

    public int size() {
        return queue.size();
    }

    public E poll() {
        return queue.poll();
    }
}
