package com.lxian.playground.algo.avltree;

import java.util.Random;

public class AvlTreeTest {

    public static void main(String[] args) {
        AvlTree<Integer> avlTree = new AvlTree<>();
        int n = 100;
        Random random = new Random();
        while (n-- > 0) {
            int key = random.nextInt(1000);
            avlTree.insert(key, String.valueOf(key));
        }
        Node<Integer>[] nodes = avlTree.preOrder();
        for (Node<Integer> node : nodes) {
            System.out.println(node);
        }

        System.out.println(avlTree.dumpShape());
    }

}
