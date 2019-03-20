package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.config.DefaultConfig;
import com.playground.hashstore.server.HashStoreServer;
import com.playground.hashstore.server.client.HashStoreClient;
import com.playground.hashstore.server.client.HashStoreError;
import com.playground.hashstore.server.client.Result;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ClientTest {

    private static HashStoreServer hashStoreServer;
    private static HashStoreClient hashStoreClient;

    @BeforeClass
    public static void initClient() {
        ConfigProvider.setConfig(DefaultConfig.config());
        hashStoreServer = new HashStoreServer(new HashStore());
        hashStoreServer.startInBackground();
        hashStoreClient = new HashStoreClient("localhost", ConfigProvider.config().getPort());
        hashStoreClient.start();
    }

    @Test
    public void test() throws HashStoreError {
        hashStoreClient.set("abcde", "fooo".getBytes());
        assertArrayEquals(hashStoreClient.get("abcde"), "fooo".getBytes());
        hashStoreClient.set("bar", "123".getBytes());
        assertArrayEquals(hashStoreClient.get("bar"), "123".getBytes());
        hashStoreClient.get("bar", (Result r) -> {
            assertArrayEquals(r.value, "123".getBytes());
            synchronized (this) {
                this.notify();
            }
        });
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public static void closeClient() {
        hashStoreServer.close();
        hashStoreClient.close();
    }
}
