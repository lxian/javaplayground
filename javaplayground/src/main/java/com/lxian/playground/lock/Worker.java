package com.lxian.playground.lock;

import java.io.IOException;
import java.util.LinkedList;

public class Worker {

    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.start();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        worker.queueTask("1");
        worker.queueTask("2");
        worker.queueTask("3");
        worker.queueTask("4");
        worker.queueTask("5");
        worker.queueTask("6");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        worker.stop();
    }

    private final Object EMPTY = new Object();
    private final LinkedList<String> tasks = new LinkedList<>();
    private boolean closing = false;

    void start() {
        Thread thread = new Thread(this::pollAndExecTask);
        thread.setDaemon(true);
        thread.start();
    }

    void stop() {
        synchronized (EMPTY) {
            closing = true;
            EMPTY.notify();
        }
    }

    private void pollAndExecTask() {
        synchronized (EMPTY) {
            for (;;) {
                if (closing) {
                    return;
                }
                while (tasks.isEmpty()) {
                    try {
                        EMPTY.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                while (!tasks.isEmpty()) {
                    System.out.println("Executing: " + tasks.pop());
                }
            }
        }
    }

    public void queueTask(String task) {
        synchronized (EMPTY) {
            tasks.push(task);
            EMPTY.notify();
        }
    }
}
