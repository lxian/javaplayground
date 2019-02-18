package com.lxian.playground.json.parser.protocol;

public interface Range<T extends Comparable<T>> {

    boolean contains(T t);
}
