package com.lxian.playground.algo.avltree;

import java.util.LinkedList;
import java.util.List;

public class IntAvlTree {

    private int size;
    private IntNode root;

    public void insert(int key) {
        root = doInsert(key, root);
    }

    public int[] preOrder() {
        int[] result = new int[size];
        doPreOrder(root, result, 0);
        return result;
    }

    private int doPreOrder(IntNode node, int[] result, int idx) {
        if (node == null) {
            return 0;
        }
        int leftCnt = doPreOrder(node.left, result, idx);
        idx += leftCnt;
        result[idx] = node.key;
        idx += 1;
        int rightCnt = doPreOrder(node.right, result, idx);
        return leftCnt + rightCnt + 1;
    }

    public String dumpShape() {
        StringBuffer sb = new StringBuffer();
        List<IntNode> cur = new LinkedList<>();
        if (root != null) {
            cur.add(root);
        }
        while (cur.size() > 0) {
            List<IntNode> next = new LinkedList<>();
            sb.append('[');
            for (IntNode node: cur) {
                sb.append(node.key + ", ");
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

    private IntNode find(int key) {
        return doFind(key, root);
    }

    private IntNode doFind(int key, IntNode node) {
        if (node == null) {
            return null;
        } else if (key == node.key) {
            return node;
        } else if (key < node.key) {
            return doFind(key, node.left);
        } else {
            return doFind(key, node.right);
        }
    }

    private IntNode doInsert(int key, IntNode node) {
        if (node == null) {
            size++;
            return new IntNode(key);
        } else if (key == node.key) {
            return node;
        } else if (key < node.key) {
            node.left = doInsert(key, node.left);
        } else {
            node.right = doInsert(key, node.right);
        }

        updateHeight(node);
        int diff = height(node.left) - height(node.right);
        if (Math.abs(diff) < 2) {
            return node;
        } else if (diff > 1 && key < node.left.key) {
            return rightRotate(node);
        } else if (diff > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        } else if (diff < -1 && key > node.right.key) {
            return leftRotate(node);
        } else if (diff < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        } else {
            throw new RuntimeException("No recognized unbalanced pattern");
        }
    }

    private IntNode leftRotate(IntNode node) {
        IntNode pivot = node.right;

        node.right = pivot.left;
        updateHeight(node);

        pivot.left = node;
        updateHeight(pivot);

        return pivot;
    }

    private IntNode rightRotate(IntNode node) {
        IntNode pivot = node.left;

        node.left = pivot.right;
        updateHeight(node);

        pivot.right = node;
        updateHeight(pivot);

        return pivot;
    }

    private int height(IntNode node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(IntNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }
}
