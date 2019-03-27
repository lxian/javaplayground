package com.lxian.playground.algo.avltree;

import java.util.Random;

public class IntAvlTreeTest {

    public static void main(String[] args) {
        IntAvlTree intAvlTree = new IntAvlTree();
        intAvlTree.insert(4);
        int n = 100;
        Random random = new Random();
        while (n-- > 0) {
            intAvlTree.insert(random.nextInt(1000));
        }
        int[] nums = intAvlTree.preOrder();
        for (int i = 0 ; i < nums.length; i++) {
            System.out.println(nums[i]);
        }

        System.out.println(intAvlTree.dumpShape());
    }

}
