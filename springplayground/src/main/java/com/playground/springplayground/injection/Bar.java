package com.playground.springplayground.injection;

public class Bar {

    int val = 99;

    int key;

    Foo foo;

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }
}
