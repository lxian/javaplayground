import com.lxian.playground.lock.SleepUntils;
import com.lxian.playground.lock.TwinsLock;
import org.junit.Test;

import java.util.concurrent.locks.Lock;

public class TestLock {

    @Test
    public void testTwinsLock() {
        final Lock lock = new TwinsLock();

        class Worker extends Thread {
            @Override
            public void run() {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().toString());
                    SleepUntils.second(5);
                } finally {
                    lock.unlock();
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            Thread t = new Worker();
            t.setDaemon(true);
            t.start();
        }

        for(;;) {
            SleepUntils.second(1);
            System.out.println("..");
        }
    }

}
