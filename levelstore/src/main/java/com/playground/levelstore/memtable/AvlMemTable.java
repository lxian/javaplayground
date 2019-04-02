package com.playground.levelstore.memtable;

import com.playground.levelstore.avltree.AvlTree;
import com.playground.levelstore.avltree.Node;

public class AvlMemTable implements MemTable {

    private boolean frozen = false;
    private AvlTree<String> avlTree = new AvlTree<String>();

    public void put(String key, byte[] value) {
        if (frozen) {
            throw new UnsupportedOperationException();
        }
        avlTree.insert(key, value);
    }

    public byte[] get(String key) {
        return (byte[]) avlTree.find(key);
    }

    public void travelThrough(MemTableEntryConsumer memTableEntryConsumer) {
        Node[] nodes = avlTree.preOrder();
        for (Node node: nodes) {
            memTableEntryConsumer.consumer((String)node.getKey(), (byte[])node.getValue());
        }
    }

    public void markImmutable() {
        frozen = true;
    }

    public long size() {
        return (long) avlTree.getSize();
    }
}
