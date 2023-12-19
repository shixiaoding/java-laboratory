package com.ding.coding.array_list;

import java.util.Arrays;

public class ArrayList<E> implements List<E>  {

    /**
     * 初始化空间个数
     */
    public static final int DEFAULT_CAPACITY = 8;

    /**
     * 空元素
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};


    /**
     * ArrayList 元素数组缓存区
     */
    transient Object[] elementData;

    /**
     * List 集合元素数量
     */
    private int size;

    public ArrayList() {
        // 默认给个空的元素，当开始添加元素的时候在初始化长度
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    @Override
    public boolean add(E e) {
        // 确保内部容量
        int minCapacity = size + 1;
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        // 判断是否需要扩容
        if (minCapacity - elementData.length > 0) {
            int oldCapacity = elementData.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - minCapacity < 0) {
                newCapacity = minCapacity;
            }
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
        // 添加元素
        elementData[size++] = e;
        return false;
    }

    @Override
    public E remove(int index) {
        E oldValue = (E) elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            // 从原始数组的某个位置，拷贝到目标对象的某个位置开始后n个元素
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null; // clear to let GC do its work
        return oldValue;
    }

    @Override
    public E get(int index) {
        return (E) elementData[index];
    }
}
