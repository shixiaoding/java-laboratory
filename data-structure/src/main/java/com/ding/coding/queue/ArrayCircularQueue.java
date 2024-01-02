package com.ding.coding.queue;

public class ArrayCircularQueue implements Queue{

    private Object[] array;
    private int capacity;
    // 头结点指向
    private int head;
    private int tail;

    public ArrayCircularQueue() {
        this.capacity = 1024;
        this.array = new Object[this.capacity];
    }

    public ArrayCircularQueue(int capcity) {
        this.capacity = capcity;
        this.array = new Object[capcity];
    }

    @Override
    public boolean add(Object o) {
        if ((tail + 1) % capacity == head) {
            return false;
        }
        array[tail] = o;
        tail = (tail + 1) % capacity;
        return true;
    }

    public Boolean addAndResize(Object o) {
        array[tail] = o;
        if ((tail = (tail + 1) % capacity) == head) {
//            resize();
        }
        return true;
    }


    @Override
    public boolean offer(Object o) {
        return false;
    }

    @Override
    public Object poll() {
        if (head == tail) {
            return null;
        }
        Object obj = array[head];
        array[head] = null;
        head = (head + 1) % capacity;
        return obj;
    }

//    // 扩容
//    private void resize() {
//        // 说明还有空间
//        if (head != tail) {
//            return;
//        }
//        int oldCapcity = this.capacity;
//        int newCapcity = this.capacity * 2;
//        Object[] newArray = new Object[newCapcity];
//        for (int i = head; i < oldCapcity; i++) {
//            newArray[i - head] = array[i];
//        }
//        for (int i = 0; i < head; i++) {
//            newArray[capacity - head + i] = array[i];
//        }
//        this.capacity = newCapcity;
//        this.array = newArray;
//        this.head = 0;
//        this.tail = oldCapcity;
//    }

    @Override
    public Object peek() {
        return null;
    }
}
