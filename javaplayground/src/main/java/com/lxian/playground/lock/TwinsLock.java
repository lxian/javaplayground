package com.lxian.playground.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TwinsLock implements Lock {

    private static class Sync extends AbstractQueuedSynchronizer {

        public Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("count must be larger than 0");
            }
            setState(count);
        }

        @Override
        protected int tryAcquireShared(int arg) {
            final int curState = getState();
            final int nextState = curState - arg;
            if (nextState < 0) {
                return -1;
            }
            if (compareAndSetState(curState, nextState)) {
                return nextState;
            }
            return -1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            for (;;) {
                final int curState = getState();
                final int nextState = curState + arg;
                if (compareAndSetState(curState, nextState)) {
                    return true;
                }
            }
        }
    }

    private Sync twinSync = new Sync(2);


    @Override
    public void lock() {
        twinSync.acquireShared(1);
    }

    @Override
    public void unlock() {
        twinSync.releaseShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    @Override
    public Condition newCondition() {
        return null;
    }
}
