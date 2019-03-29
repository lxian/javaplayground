package com.lxian.playground.algo.avltree;

import java.util.LinkedList;
import java.util.List;

public class AvlTree<K extends Comparable<K>> {

    private int size;
    private Node<K> root;

    public void insert(K key, Object value) {
        root = doInsert(key, value, root);
    }

    public Object find(K key) {
        Node<K> found = doFind(key, root);
        return found == null ? null : found.value;
    }

    private Node<K> doFind(K key, Node<K> node) {
        if (node == null) {
            return null;
        } else if (key == node.key || key.compareTo(node.key) == 0) {
            return node;
        } else if (key.compareTo(node.key) < 0) {
            return doFind(key, node.left);
        } else {
            return doFind(key, node.right);
        }
    }

    private Node<K> doInsert(K key, Object value, Node<K> node) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        } else if (key == node.key || key.compareTo(node.key) == 0) {
            return node;
        } else if (key.compareTo(node.key) < 0) {
            node.left = doInsert(key, value, node.left);
        } else {
            node.right = doInsert(key, value, node.right);
        }

        updateHeight(node);
        int diff = height(node.left) - height(node.right);
        if (Math.abs(diff) < 2) {
            return node;
        } else if (diff > 1 && key.compareTo(node.left.key) < 0) {
            return rightRotate(node);
        } else if (diff > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        } else if (diff < -1 && key.compareTo(node.right.key) > 0) {
            return leftRotate(node);
        } else if (diff < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        } else {
            throw new RuntimeException("No recognized unbalanced pattern");
        }
    }

    private Node<K> leftRotate(Node<K> node) {
        Node<K> pivot = node.right;

        node.right = pivot.left;
        updateHeight(node);

        pivot.left = node;
        updateHeight(pivot);

        return pivot;
    }

    private Node<K> rightRotate(Node<K> node) {
        Node<K> pivot = node.left;

        node.left = pivot.right;
        updateHeight(node);

        pivot.right = node;
        updateHeight(pivot);

        return pivot;
    }

    private int height(Node<K> node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(Node<K> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    @SuppressWarnings("unchecked")
    public Node[] preOrder() {
        Node[] result = new Node[size];
        doPreOrder(root, result, 0);
        return result;
    }

    private int doPreOrder(Node node, Node[] result, int idx) {
        if (node == null) {
            return 0;
        }
        int leftCnt = doPreOrder(node.left, result, idx);
        idx += leftCnt;
        result[idx] = node;
        idx += 1;
        int rightCnt = doPreOrder(node.right, result, idx);
        return leftCnt + rightCnt + 1;
    }

    public String dumpShape() {
        StringBuffer sb = new StringBuffer();
        List<Node<K>> cur = new LinkedList<>();
        if (root != null) {
            cur.add(root);
        }
        while (cur.size() > 0) {
            List<Node<K>> next = new LinkedList<>();
            sb.append('[');
            for (Node<K> node: cur) {
                sb.append(node);
                if (node.left != null) {
                    next.add(node.left);
                }
                if (node.right != null) {
                    next.add(node.right);
                }
            }
            sb.append(']');
            sb.append('\n');
            cur = next;
        }
        return sb.toString();
    }

}
