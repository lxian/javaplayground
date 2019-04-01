package com.playground.levelstore.memtable;


import java.util.Random;

public class MemTableTest {

    public static void main(String[] args) {
        MemTable memTable = new AvlMemTable();

        int n = 100;
        Random random = new Random();
        while (n-- > 0) {
            int key = random.nextInt(1000);
            memTable.put(String.valueOf(key), String.valueOf(key).getBytes());
        }
        memTable.markImmutable();
        memTable.travelThrough((k, v) -> {
            System.out.println(k + ": " + new String(v));
        });
    }

}
