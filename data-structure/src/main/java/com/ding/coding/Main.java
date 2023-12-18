package com.ding.coding;

import com.ding.coding.list.LinkedList;

public class Main {
    public static void main(String[] args) {

        LinkedList<Object> xxxList = new LinkedList<>();

        xxxList.add("asdad");
        xxxList.add("xxxx");
        xxxList.add("3333");
        xxxList.add(1);

        xxxList.printLinkList();
    }
}