package multithreading;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Q17: Demonstrate CompletableFuture patterns
 * Shows asynchronous programming patterns with CompletableFuture
 */
public class CompletableFutureExample {
    public static void main(String[] args) {
        System.out.println("1. Basic CompletableFuture:");
        basicCompletableFuture();

        System.out.println("\n2. CompletableFuture chaining:");
        chainedCompletableFuture();

        System.out.println("\n3. Combining multiple CompletableFutures:");
        combiningCompletableFutures();

        System.out.println("\n4. Exception handling in CompletableFuture:");
        exceptionHandling();

        System.out.println("\n5. Parallel processing with CompletableFuture:");
        parallelProcessing();
    }

    private static void basicCompletableFuture() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Result of the asynchronous computation";
        });

        try {
            System.out.println("Waiting for the result...");
            String result = future.get(1, TimeUnit.SECONDS);
            System.out.println("Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void chainedCompletableFuture() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello";
        }).thenApplyAsync(s -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return s + " World";
        }).thenApplyAsync(s -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return s + "!";
        });

        try {
            System.out.println("Result of chained operations: " + future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void combiningCompletableFutures() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Hello";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "World";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(400);
            return "!";
        });

        // Combine futures using thenCombine
        CompletableFuture<String> combinedFuture1 = future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2);
        CompletableFuture<String> combinedFuture2 = combinedFuture1.thenCombine(future3, (s1, s2) -> s1 + s2);

        try {
            System.out.println("Result of thenCombine: " + combinedFuture2.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Combine futures using allOf
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);
        CompletableFuture<String> allResult = allFutures.thenApply(v -> {
            try {
                return future1.get() + " " + future2.get() + future3.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        try {
            System.out.println("Result of allOf: " + allResult.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void exceptionHandling() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Something went wrong!");
            }
            return "Success";
        }).exceptionally(ex -> {
            System.out.println("Exception caught: " + ex.getMessage());
            return "Recovered from exception";
        });

        try {
            System.out.println("Result with exception handling: " + future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        CompletableFuture<String> futureWithHandle = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Something went wrong in handle!");
            }
            return "Success";
        }).handle((result, ex) -> {
            if (ex != null) {
                System.out.println("Exception handled: " + ex.getMessage());
                return "Recovered using handle";
            }
            return result;
        });

        try {
            System.out.println("Result with handle: " + futureWithHandle.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parallelProcessing() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(numbers.size(), 10));

        try {
            // Convert each number asynchronously
            List<CompletableFuture<Integer>> futures = numbers.stream()
                    .map(n -> CompletableFuture.supplyAsync(() -> {
                        sleep(100); // Simulate processing time
                        return n * 2;
                    }, executor))
                    .collect(Collectors.toList());

            // Combine all futures to a single future
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));

            // When all futures complete, extract and collect results
            CompletableFuture<List<Integer>> allResults = allFutures.thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));

            List<Integer> results = allResults.get();
            System.out.println("Parallel processing results: " + results);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
