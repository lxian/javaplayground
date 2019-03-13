package com.lxian.playground.algo.minheap;


public class IntegerMinHeap {

    private int[] heap;
    private int size;
    private int capacity;

    private static final int INITIAL_CAP = 4;
    public IntegerMinHeap() {
        this(INITIAL_CAP);
    }

    public IntegerMinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
    }

    public void push(int val) {
        if (size == capacity) {
            grow();
        }

        int idx = size++;
        heap[idx] = val;
        siftUp(idx);
    }

    public int pop() {
        int top = heap[0];
        heap[0] = heap[size-1];
        size--;
        siftDown(0);
        return top;
    }

    public int top() {
        return heap[0];
    }

    public int size() {
        return size;
    }

    private void grow() {
        int newCapacity = capacity << 1;
        int[] newHeap = new int[newCapacity];
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
                swapWith = min(idx, leftChildIdx, rightChildIdx);
            } else if (leftChildIdx < size && rightChildIdx >= size) {
                swapWith = min(idx, leftChildIdx);
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

    private void siftUp(int idx) {
        while (idx != 0) {
            int parentIdx = parentIdx(idx);
            if (heap[parentIdx] > heap[idx]) {
                swap(parentIdx, idx);
                idx = parentIdx;
            } else {
                break;
            }
        }
    }

    private int min(int i, int j) {
        return heap[i] > heap[j] ? j : i;
    }

    private int min(int i, int j, int k) {
        return heap[i] > heap[j] ? (heap[j] > heap[k] ? k : j) : (heap[i] > heap[k] ? k : i);
    }

    private void swap(int i, int j) {
        int tmp = heap[i];
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
