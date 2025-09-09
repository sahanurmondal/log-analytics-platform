package multithreading;

/**
 * Q2: Implement a Reader-Writer lock
 * Allows multiple readers to read concurrently, but only one writer can write
 * at a time
 */
public class ReaderWriterLock {
    public static void main(String[] args) {
        SharedResource resource = new SharedResource();

        // Create reader threads
        for (int i = 0; i < 5; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    while (true) {
                        resource.read();
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Reader-" + id).start();
        }

        // Create writer threads
        for (int i = 0; i < 2; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    int value = 0;
                    while (true) {
                        resource.write(value++);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Writer-" + id).start();
        }
    }

    static class ReadWriteLock {
        private int readers = 0;
        private int writers = 0;
        private int writeRequests = 0;

        public synchronized void lockRead() throws InterruptedException {
            while (writers > 0 || writeRequests > 0) {
                wait();
            }
            readers++;
        }

        public synchronized void unlockRead() {
            readers--;
            notifyAll();
        }

        public synchronized void lockWrite() throws InterruptedException {
            writeRequests++;
            while (readers > 0 || writers > 0) {
                wait();
            }
            writeRequests--;
            writers++;
        }

        public synchronized void unlockWrite() {
            writers--;
            notifyAll();
        }
    }

    static class SharedResource {
        private final ReadWriteLock lock = new ReadWriteLock();
        private int data = 0;

        public int read() throws InterruptedException {
            try {
                lock.lockRead();
                System.out.println(Thread.currentThread().getName() + " reading: " + data);
                Thread.sleep(100); // Simulate reading time
                return data;
            } finally {
                lock.unlockRead();
            }
        }

        public void write(int value) throws InterruptedException {
            try {
                lock.lockWrite();
                System.out.println(Thread.currentThread().getName() + " writing: " + value);
                Thread.sleep(200); // Simulate writing time
                data = value;
            } finally {
                lock.unlockWrite();
            }
        }
    }
}
