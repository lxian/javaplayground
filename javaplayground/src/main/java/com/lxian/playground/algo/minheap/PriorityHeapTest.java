package com.lxian.playground.algo.minheap;

public class PriorityHeapTest {

    public static void main(String[] args) {

        PriorityHeap<Integer> integerMaxHeap = new PriorityHeap<>();
        integerMaxHeap.push(2);
        integerMaxHeap.push(8);
        integerMaxHeap.push(6);
        integerMaxHeap.push(1);
        checkEq(integerMaxHeap.top(), 8);
        checkEq(integerMaxHeap.pop(), 8);
        checkEq(integerMaxHeap.pop(), 6);
        integerMaxHeap.push(3);
        integerMaxHeap.push(5);
        integerMaxHeap.push(10);
        integerMaxHeap.push(9);
        checkEq(integerMaxHeap.pop(), 10);
        checkEq(integerMaxHeap.pop(), 9);
        checkEq(integerMaxHeap.pop(), 5);
        checkEq(integerMaxHeap.pop(), 3);
        checkEq(integerMaxHeap.pop(), 2);
        checkEq(integerMaxHeap.pop(), 1);
        integerMaxHeap.push(9);
        integerMaxHeap.push(9);
        integerMaxHeap.push(9);
        integerMaxHeap.push(1);
        checkEq(integerMaxHeap.pop(), 9);
        checkEq(integerMaxHeap.pop(), 9);
        checkEq(integerMaxHeap.pop(), 9);
        checkEq(integerMaxHeap.pop(), 1);
        checkEq(integerMaxHeap.size(), 0);
    }

    private static void checkEq(int val, int expected) {
        if (val != expected) { throw new RuntimeException(val + " != " + expected); }
        System.out.println(val + " == " + expected);
    }
}
