package com.lxian.playground.algo.minheap;

public class IntegerMinHeapTest {

    public static void main(String[] args) {

        IntegerMinHeap integerMinHeap = new IntegerMinHeap();
        integerMinHeap.push(2);
        integerMinHeap.push(8);
        integerMinHeap.push(6);
        integerMinHeap.push(1);
        checkEq(integerMinHeap.top(), 1);
        checkEq(integerMinHeap.pop(), 1);
        checkEq(integerMinHeap.pop(), 2);
        integerMinHeap.push(3);
        integerMinHeap.push(5);
        integerMinHeap.push(10);
        integerMinHeap.push(9);
        checkEq(integerMinHeap.pop(), 3);
        checkEq(integerMinHeap.pop(), 5);
        checkEq(integerMinHeap.pop(), 6);
        checkEq(integerMinHeap.pop(), 8);
        checkEq(integerMinHeap.pop(), 9);
        checkEq(integerMinHeap.pop(), 10);
        integerMinHeap.push(9);
        integerMinHeap.push(9);
        integerMinHeap.push(9);
        integerMinHeap.push(1);
        checkEq(integerMinHeap.pop(), 1);
        checkEq(integerMinHeap.pop(), 9);
        checkEq(integerMinHeap.pop(), 9);
        checkEq(integerMinHeap.pop(), 9);
        checkEq(integerMinHeap.size(), 0);
    }

    private static void checkEq(int val, int expected) {
        if (val != expected) { throw new RuntimeException(val + " != " + expected); }
        System.out.println(val + " == " + expected);
    }
}
