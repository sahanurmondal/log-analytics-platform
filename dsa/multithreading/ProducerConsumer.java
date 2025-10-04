package multithreading;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Q1: Implement a Producer-Consumer pattern using wait/notify
 * This is a classic multithreading problem where producers create resources
 * that are consumed by consumers, with synchronization to ensure correctness
 */
public class ProducerConsumer {
    private static final int MAX_SIZE = 10;
    private static final Queue<Integer> buffer = new LinkedList<>();

    public static void main(String[] args) {
        Thread producerThread = new Thread(new Producer());
        Thread consumerThread = new Thread(new Consumer());

        producerThread.start();
        consumerThread.start();
    }

    static class Producer implements Runnable {
        public void run() {
            int value = 0;
            try {
                while (true) {
                    synchronized (buffer) {
                        while (buffer.size() == MAX_SIZE) {
                            System.out.println("Buffer is full, producer waiting...");
                            buffer.wait();
                        }

                        System.out.println("Producing: " + value);
                        buffer.add(value++);
                        buffer.notifyAll();
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer implements Runnable {
        public void run() {
            try {
                while (true) {
                    synchronized (buffer) {
                        while (buffer.isEmpty()) {
                            System.out.println("Buffer is empty, consumer waiting...");
                            buffer.wait();
                        }

                        int value = buffer.poll();
                        System.out.println("Consuming: " + value);
                        buffer.notifyAll();
                    }
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
