package com.playground.levelstore.avltree;

public class Node<K extends Comparable<K>> {

    final K key;

    Object value;

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

    public K getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
