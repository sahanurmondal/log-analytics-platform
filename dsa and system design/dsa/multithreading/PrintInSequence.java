package multithreading;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

/**
 * Q12: Print in sequence
 * Implement a ZeroEvenOdd class to print 0 and even/odd numbers in sequence
 */
public class PrintInSequence {
    public static void main(String[] args) {
        int n = 10;
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(n);

        Thread threadA = new Thread(() -> {
            try {
                zeroEvenOdd.zero(System.out::print);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                zeroEvenOdd.even(System.out::print);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread threadC = new Thread(() -> {
            try {
                zeroEvenOdd.odd(System.out::print);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();
    }

    static class ZeroEvenOdd {
        private int n;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition zeroTurn = lock.newCondition();
        private final Condition evenTurn = lock.newCondition();
        private final Condition oddTurn = lock.newCondition();
        private boolean isZeroTurn = true;
        private boolean isOddTurn = true;
        private int current = 1;

        public ZeroEvenOdd(int n) {
            this.n = n;
        }

        public void zero(IntConsumer printNumber) throws InterruptedException {
            for (int i = 1; i <= n; i++) {
                lock.lock();
                try {
                    while (!isZeroTurn) {
                        zeroTurn.await();
                    }

                    printNumber.accept(0);
                    isZeroTurn = false;

                    if (isOddTurn) {
                        oddTurn.signal();
                    } else {
                        evenTurn.signal();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        public void even(IntConsumer printNumber) throws InterruptedException {
            for (int i = 2; i <= n; i += 2) {
                lock.lock();
                try {
                    while (isZeroTurn || isOddTurn) {
                        evenTurn.await();
                    }

                    printNumber.accept(i);
                    isZeroTurn = true;
                    isOddTurn = true;

                    zeroTurn.signal();
                } finally {
                    lock.unlock();
                }
            }
        }

        public void odd(IntConsumer printNumber) throws InterruptedException {
            for (int i = 1; i <= n; i += 2) {
                lock.lock();
                try {
                    while (isZeroTurn || !isOddTurn) {
                        oddTurn.await();
                    }

                    printNumber.accept(i);
                    isZeroTurn = true;
                    isOddTurn = false;

                    zeroTurn.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
