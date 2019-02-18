package com.lxian.playground.lock;

public class SleepUntils {

    public static void second(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
