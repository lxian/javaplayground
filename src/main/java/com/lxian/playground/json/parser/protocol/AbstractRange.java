package com.lxian.playground.json.parser.protocol;

import java.util.LinkedList;
import java.util.List;

public class AbstractRange<T extends Comparable<T>> implements Range<T> {

    private T lower;

    private T upper;

    public AbstractRange(T lower, T upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean contains(T t) {
        if (lower != null && lower.compareTo(t) > 0) {
            return false;
        }

        if (upper != null && upper.compareTo(t) < 0) {
            return false;
        }

        return true;
    }

    public MultiRange<T> or(AbstractRange<T> otherRange) {
        MultiRange<T> multiRange = new MultiRange<T>();
        multiRange.or(this);
        multiRange.or(otherRange);
        return multiRange;
    }

    public static class MultiRange<U extends Comparable<U>> implements Range<U> {

        List<AbstractRange<U>> ranges = new LinkedList<AbstractRange<U>>();

        public MultiRange<U> or(AbstractRange<U> range) {
            ranges.add(range);
            return this;
        }

        public boolean contains(U t) {
            for (Range<U> range : ranges) {
                if (range.contains(t)) {
                    return true;
                }
            }

            return false;
        }
    }
}
