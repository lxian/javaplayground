package com.lxian.playground.algo.avltree;

public class Node<K extends Comparable<K>> {

    final K key;

    final Object value;

    int height;

    Node<K> left;

    Node<K> right;

    public Node(K key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("{%s: %s}", key, value);
    }
}
