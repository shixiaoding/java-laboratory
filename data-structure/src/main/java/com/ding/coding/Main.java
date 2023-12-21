package com.ding.coding;

import com.ding.coding.array_list.ArrayList;
import com.ding.coding.linked_list.LinkedList;
import com.ding.coding.stack.ArrayDeque;

public class Main {
    public static void main(String[] args) {

//        LinkedList<Object> xxxList = new LinkedList<>();
//
//        xxxList.add("asdad");
//        xxxList.add("xxxx");
//        xxxList.add("3333");
//        xxxList.add(1);
//
//        xxxList.printLinkList();

        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.push(1);
        deque.push(2);
        deque.push(3);
        deque.push(4);
        deque.push(5);
        deque.push(6);
        deque.push(7);
        deque.push(8);
        deque.push(9);

        deque.pop();
        deque.pop();
        deque.push(10);
    }
}