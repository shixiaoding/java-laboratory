package com.ding.coding.queue;

public class LinkedQueue<E> implements Queue<E> {

    private Node head;

    private Node tail;

    private int size;


    public LinkedQueue() {
        head = new Node(null, null);
        tail = head;
    }

    @Override
    public boolean add(E e) {
        tail.next = new Node(e, null);
        tail = tail.next;
        size++;
        return true;
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E poll() {
        Node next = head.next;
        if (next == null) {
            return null;
        }

        head = next.next;
        size--;
        return (E) next.item;
    }

    @Override
    public E peek() {
        return null;
    }

    public static class Node<E> {
        private E item;
        private Node next;

        public Node(E item, Node next) {
            this.item = item;
            this.next = next;
        }
    }
}
