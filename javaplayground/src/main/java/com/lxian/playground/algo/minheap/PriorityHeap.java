package com.lxian.playground.algo.minheap;


public class PriorityHeap<T extends Comparable<T>> {

    private Object[] heap;
    private int size;
    private int capacity;

    private static final int INITIAL_CAP = 4;
    public PriorityHeap() {
        this(INITIAL_CAP);
    }

    public PriorityHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new Object[capacity];
        this.size = 0;
    }

    public void push(T val) {
        if (size == capacity) {
            grow();
        }

        int idx = size++;
        heap[idx] = val;
        siftUp(idx);
    }

    public T pop() {
        T top = (T) heap[0];
        heap[0] = heap[size-1];
        size--;
        siftDown(0);
        return top;
    }

    public T top() {
        return (T) heap[0];
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void grow() {
        int newCapacity = capacity << 1;
        Object[] newHeap = new Object[newCapacity];
        System.arraycopy(heap, 0, newHeap, 0, capacity);
        heap = newHeap;
        capacity = newCapacity;
    }

    private void siftDown(int idx) {
        for (;;) {
            int leftChildIdx = leftChildIdx(idx);
            int rightChildIdx = rightChildIdx(idx);
            int swapWith = 0;
            if (leftChildIdx < size && rightChildIdx < size) {
                swapWith = highest(idx, leftChildIdx, rightChildIdx);
            } else if (leftChildIdx < size && rightChildIdx >= size) {
                swapWith = highest(idx, leftChildIdx);
            } else {
                break;
            }

            if (swapWith == idx) {
                break;
            }

            swap(idx, swapWith);
            idx = swapWith;
        }
    }

    private T getVal(int idx) {
        return (T) heap[idx];
    }

    private void siftUp(int idx) {
        while (idx != 0) {
            int parentIdx = parentIdx(idx);
            if (getVal(parentIdx).compareTo(getVal(idx)) < 0) {
                swap(parentIdx, idx);
                idx = parentIdx;
            } else {
                break;
            }
        }
    }

    private int highest(int i, int j) {
        return getVal(i).compareTo(getVal(j)) > 0 ? i : j;
    }

    private int highest(int i, int j, int k) {
        return getVal(i).compareTo(getVal(j)) > 0 ? highest (i, k) : highest(j, k);
    }

    private void swap(int i, int j) {
        Object tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private int leftChildIdx(int idx) {
        return (idx << 1) + 1;
    }

    private int rightChildIdx(int idx) {
        return (idx << 1) + 2;
    }

    private int parentIdx(int idx) {
        return (idx - 1) >>> 1;
    }
}
