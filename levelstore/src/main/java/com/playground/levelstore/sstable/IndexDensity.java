package com.playground.levelstore.sstable;

import com.playground.levelstore.config.ConfigProvider;

public class IndexDensity {

    private int counter = 0;

    public IndexDensity() {
    }

    boolean shouldAddIndex() {
        return counter++ % ConfigProvider.config().getIndexDensity() == 0;
    }
}
