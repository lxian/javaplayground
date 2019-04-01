package com.playground.levelstore;

import com.playground.levelstore.config.TestConfig;
import org.junit.Test;

import java.util.Random;

public class LevelStoreTest {

    @Test
    public void test() {
        LevelStore levelStore = new LevelStore(new TestConfig());
        int n = 300;
        String[] keys = new String[n];
        while (n-- > 0) {
            String key = String.valueOf(n);
            keys[n] = key;
        }
//        while (n-- > 0) {
//            String key = String.valueOf(n);
//            byte[] value = ("V" + key).getBytes();
//            keys[n] = key;
//            levelStore.put(String.valueOf(n), value);
//        }
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        for (int i = 0; i < 300; i++) {
            byte[] value = levelStore.get(keys[i]);
            if (value == null || !new String(value).equals("V" + keys[i])) {
                System.out.println(keys[i]);
            }
        }

        levelStore.flushAllToDisk();

    }

}
