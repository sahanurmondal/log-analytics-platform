package multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Q11: Print in order
 * Given three functions that print A, B, and C, ensure they always print in
 * order ABC
 * regardless of the order of thread execution
 */
public class PrintInOrder {
    public static void main(String[] args) {
        System.out.println("Method 1: Using CountDownLatch");
        Foo1 foo1 = new Foo1();
        runTest(foo1);

        System.out.println("\nMethod 2: Using Semaphores");
        Foo2 foo2 = new Foo2();
        runTest(foo2);
    }

    private static void runTest(Foo foo) {
        Thread t3 = new Thread(() -> {
            try {
                foo.third(() -> System.out.println("C"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                foo.second(() -> System.out.println("B"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t1 = new Thread(() -> {
            try {
                foo.first(() -> System.out.println("A"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start threads in different orders
        t3.start();
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    interface Foo {
        void first(Runnable printFirst) throws InterruptedException;

        void second(Runnable printSecond) throws InterruptedException;

        void third(Runnable printThird) throws InterruptedException;
    }

    // Solution using CountDownLatch
    static class Foo1 implements Foo {
        private CountDownLatch latch1;
        private CountDownLatch latch2;

        public Foo1() {
            latch1 = new CountDownLatch(1);
            latch2 = new CountDownLatch(1);
        }

        public void first(Runnable printFirst) throws InterruptedException {
            printFirst.run();
            latch1.countDown();
        }

        public void second(Runnable printSecond) throws InterruptedException {
            latch1.await();
            printSecond.run();
            latch2.countDown();
        }

        public void third(Runnable printThird) throws InterruptedException {
            latch2.await();
            printThird.run();
        }
    }

    // Solution using Semaphores
    static class Foo2 implements Foo {
        private Semaphore sem1;
        private Semaphore sem2;

        public Foo2() {
            sem1 = new Semaphore(0);
            sem2 = new Semaphore(0);
        }

        public void first(Runnable printFirst) throws InterruptedException {
            printFirst.run();
            sem1.release();
        }

        public void second(Runnable printSecond) throws InterruptedException {
            sem1.acquire();
            printSecond.run();
            sem2.release();
        }

        public void third(Runnable printThird) throws InterruptedException {
            sem2.acquire();
            printThird.run();
        }
    }
}
