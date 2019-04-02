package com.playground.levelstore;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LevelStoreTest {

    Random random = new Random();

    @Test
    public void test() throws IOException {
        LevelStore levelStore = new LevelStore(new TestConfig());
        int n = 10000;
        Map<String, String> map = new HashMap<>();
        String[] keys = new String[n];
        while (n-- > 0) {
            String key = String.format("%03d", random.nextInt(1000));
            String val = randomString();
//            System.out.println(n + ": " + key + " : " + val);
            map.put(key, val);
            levelStore.put(key, val.getBytes());
        }

        map.forEach((k, v) -> {
            assertEquals(v, new String(levelStore.get(k)));
        });

        levelStore.flushAllToDisk();

        System.in.read();
    }

    String randomString() {
        StringBuffer sb = new StringBuffer();
        int len = random.nextInt(19) + 1;
        while (len-- > 0) {
            sb.append(random.nextInt(26) + 'a');
        }
        return sb.toString();
    }
}
