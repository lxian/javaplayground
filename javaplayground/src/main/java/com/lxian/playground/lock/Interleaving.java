package com.lxian.playground.lock;


public class Interleaving {

    public static void main(String[] args) {
        Interleaving interleaving = new Interleaving();

        Thread thread1 = new Thread(() -> {
            synchronized (interleaving) {
                for (;;) {
                    while (interleaving.val % 2 == 0) {
                        try {
                            interleaving.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (interleaving.val == interleaving.upper) {
                            interleaving.notifyAll();
                            return;
                        }
                    }

                    System.out.println("Thread1: " + interleaving.val);
                    interleaving.val += 1;
                    interleaving.notify();
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            synchronized (interleaving) {
                for (;;) {
                    while (interleaving.val % 2 == 1) {
                        try {
                            interleaving.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (interleaving.val == interleaving.upper) {
                            interleaving.notifyAll();
                            return;
                        }
                    }

                    System.out.println("Thread2: " + interleaving.val);
                    interleaving.val += 1;
                    interleaving.notify();
                }
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int val = 0;
    private int upper = 10;
}
