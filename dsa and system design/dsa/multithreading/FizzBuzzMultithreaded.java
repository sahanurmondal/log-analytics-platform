package multithreading;

import java.util.function.IntConsumer;

/**
 * Q13: Fizz Buzz Multithreaded
 * Implement multithreaded Fizz Buzz with four threads
 */
public class FizzBuzzMultithreaded {
    public static void main(String[] args) {
        int n = 30;
        FizzBuzz fizzBuzz = new FizzBuzz(n);

        Thread threadA = new Thread(() -> {
            try {
                fizzBuzz.fizz(() -> System.out.print("fizz "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                fizzBuzz.buzz(() -> System.out.print("buzz "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread threadC = new Thread(() -> {
            try {
                fizzBuzz.fizzbuzz(() -> System.out.print("fizzbuzz "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread threadD = new Thread(() -> {
            try {
                fizzBuzz.number(num -> System.out.print(num + " "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();
        threadD.start();
    }

    static class FizzBuzz {
        private int n;
        private int current = 1;
        private final Object lock = new Object();

        public FizzBuzz(int n) {
            this.n = n;
        }

        public void fizz(Runnable printFizz) throws InterruptedException {
            synchronized (lock) {
                while (current <= n) {
                    if (current % 3 == 0 && current % 5 != 0) {
                        printFizz.run();
                        current++;
                        lock.notifyAll();
                    } else {
                        lock.wait();
                    }
                }
            }
        }

        public void buzz(Runnable printBuzz) throws InterruptedException {
            synchronized (lock) {
                while (current <= n) {
                    if (current % 3 != 0 && current % 5 == 0) {
                        printBuzz.run();
                        current++;
                        lock.notifyAll();
                    } else {
                        lock.wait();
                    }
                }
            }
        }

        public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
            synchronized (lock) {
                while (current <= n) {
                    if (current % 3 == 0 && current % 5 == 0) {
                        printFizzBuzz.run();
                        current++;
                        lock.notifyAll();
                    } else {
                        lock.wait();
                    }
                }
            }
        }

        public void number(IntConsumer printNumber) throws InterruptedException {
            synchronized (lock) {
                while (current <= n) {
                    if (current % 3 != 0 && current % 5 != 0) {
                        printNumber.accept(current);
                        current++;
                        lock.notifyAll();
                    } else {
                        lock.wait();
                    }
                }
            }
        }
    }
}
