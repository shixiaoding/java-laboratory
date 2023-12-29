package com.ding.coding.queue;

public interface BlockingQueue<E> extends Queue<E> {

    boolean add(E e);

    boolean offer(E e);
}
