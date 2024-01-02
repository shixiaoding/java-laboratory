package com.ding.coding.queue;

public class ArrayQueue implements Queue {
    private Object[] array;

    private int capacity;

    // head 指向第一个数据结点的位置， tail 指向最后一个数据结点 之后的位置

    private int head;

    private int tail;

    public ArrayQueue() {
        this.capacity = 1024;
        this.array = new Object[this.capacity];
    }

    /**
     * tail 指向数组最后位置时，需要触发扩容或数组搬移
     * 1. head!=0 说明数组还有剩余的空间，将 head 搬运到队列 array[0]
     * 2. head==0  说明数组没有剩余的空间，扩容
     */
    @Override
    public boolean add(Object o) {
        if (tail == capacity) {
            if (head == 0 ) {
                resize();
            } else {
                rewind();
            }
        }
        array[tail++] = o;
        return true;
    }

    // 扩容
    private void resize() {
        int oldCapcity = this.capacity;
        int newCapcity = this.capacity * 2;
        Object[] newArray = new Object[newCapcity];
        for (int i = 0; i < oldCapcity; i++) {
            newArray[i] = array[i];
        }
        this.capacity = newCapcity;
        this.array = newArray;
    }

    // 将 head 搬运到队列 array[0]
    private void rewind() {
        for (int i = head; i < tail; i++) {
            array[i - head] = array[i];
            array[i] = null;
        }
        tail -=  head;
        head = 0;
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

        Object o = array[head];
        array[head] = null;
        head++;
        return o;
    }

    @Override
    public Object peek() {
        return null;
    }
}
