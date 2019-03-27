package com.lxian.playground.lock;


import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Interleaving2 {

    public static void main(String[] args) {

        int size = 10;
        ConditionTable conditionTable = new ConditionTable(size);
        Lock lock = new ReentrantLock();

        while (size-- > 0) {
            Worker worker = new Worker(lock.newCondition(), conditionTable, lock, size);
            worker.start();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int val = 0;
    private int upper = 10;

    static class ConditionTable {
        int counter = 0;
        int size;
        Condition[] conditions;

        ConditionTable(int size) {
            this.size = size;
            conditions = new Condition[size];
        }

        Condition nextConditionToWakeUp() {
            counter = ++counter % size;
            return conditions[counter];
        }

        void register(int idx, Condition condition) {
            conditions[idx] = condition;
        }

        int getCounter() {
            return counter;
        }
    }

    static class Worker extends Thread {

        private final Condition condition;
        private final ConditionTable conditionTable;
        private final Lock lock;
        private final int seq;

        Worker(Condition condition, ConditionTable conditionTable, Lock lock, int seq) {
            super();
            this.condition = condition;
            this.conditionTable = conditionTable;
            this.lock = lock;
            this.seq = seq;
            this.conditionTable.register(seq, condition);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                lock.lock();
                while (conditionTable.getCounter() != seq) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                print(seq, conditionTable.getCounter());
                conditionTable.nextConditionToWakeUp().signal();

                lock.unlock();
            }
        }

        private static Random random = new Random();
        private static void print(int seq, int counter) {
            System.out.println("Worker " + seq + ": print " + counter);
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
