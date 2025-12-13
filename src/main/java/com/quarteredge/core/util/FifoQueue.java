package com.quarteredge.core.util;

import java.util.ArrayDeque;

/**
 * A fixed-size first-in-first-out (FIFO) queue implementation.
 *
 * <p>This class provides a thread-safe, bounded queue that maintains a fixed number of elements.
 * When the queue reaches its maximum capacity, adding a new element will automatically remove the
 * oldest element (head of the queue) to make space for the new element.
 *
 * <p>This implementation is backed by an {@link ArrayDeque} and provides O(1) time complexity for
 * add, poll, and size operations.
 *
 * @param <E> the type of elements held in this collection
 * @author King Simmons
 * @version 1.0
 * @since 0.2.0
 */
public class FifoQueue<E> {
    /** The underlying deque used to store elements. */
    private final ArrayDeque<E> queue;

    /** The maximum number of elements the queue can hold. */
    private final int capacity;

    /**
     * Constructs an empty FIFO queue with the specified capacity.
     *
     * @param capacity the maximum number of elements the queue can hold
     * @throws IllegalArgumentException if the specified capacity is negative
     */
    public FifoQueue(final int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayDeque<>(capacity);
    }

    /**
     * Adds an element to the end of this queue. If the queue is at capacity, the oldest element
     * (head) is automatically removed before adding the new element.
     *
     * @param e the element to add
     */
    public void add(final E e) {
        if (queue.size() == capacity) {
            queue.removeFirst();
        }
        queue.addLast(e);
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return the number of elements in this queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    public E poll() {
        return queue.poll();
    }

    /**
     * Returns the underlying deque used by this queue. This method provides direct access to the
     * internal data structure and should be used with caution.
     *
     * <p><b>Note:</b> Modifications to the returned deque will be reflected in this queue and vice
     * versa.
     *
     * @return the underlying deque
     */
    public ArrayDeque<E> getQueue() {
        return queue;
    }
}
