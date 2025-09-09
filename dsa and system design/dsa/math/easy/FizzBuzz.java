package math.easy;

import java.util.*;

/**
 * LeetCode 412: Fizz Buzz
 * https://leetcode.com/problems/fizz-buzz/
 * 
 * Companies: Apple, Amazon, Microsoft, Google, Meta, Bloomberg
 * Frequency: Very High (Asked in 1000+ interviews)
 *
 * Description:
 * Given an integer n, return a string array answer (1-indexed) where:
 * - answer[i] == "FizzBuzz" if i is divisible by 3 and 5.
 * - answer[i] == "Fizz" if i is divisible by 3.
 * - answer[i] == "Buzz" if i is divisible by 5.
 * - answer[i] == i (as a string) otherwise.
 *
 * Constraints:
 * - 1 <= n <= 10^4
 * 
 * Follow-up Questions:
 * 1. How would you handle multiple divisibility rules efficiently?
 * 2. Can you implement it without using modulo operator?
 * 3. What if the rules are configurable at runtime?
 * 4. How to optimize for very large values of n?
 * 5. Can you implement a streaming version for infinite sequences?
 */
public class FizzBuzz {

    // Approach 1: Simple modulo approach - O(n) time, O(1) extra space
    public List<String> fizzBuzz(int n) {
        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            if (i % 15 == 0) {
                result.add("FizzBuzz");
            } else if (i % 3 == 0) {
                result.add("Fizz");
            } else if (i % 5 == 0) {
                result.add("Buzz");
            } else {
                result.add(String.valueOf(i));
            }
        }

        return result;
    }

    // Approach 2: String concatenation approach - O(n) time, O(1) extra space
    public List<String> fizzBuzzConcat(int n) {
        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            String output = "";

            if (i % 3 == 0) {
                output += "Fizz";
            }
            if (i % 5 == 0) {
                output += "Buzz";
            }
            if (output.isEmpty()) {
                output = String.valueOf(i);
            }

            result.add(output);
        }

        return result;
    }

    // Approach 3: Counter approach (avoids modulo) - O(n) time, O(1) extra space
    public List<String> fizzBuzzCounter(int n) {
        List<String> result = new ArrayList<>(n);

        int fizzCount = 0;
        int buzzCount = 0;

        for (int i = 1; i <= n; i++) {
            fizzCount++;
            buzzCount++;

            String output = "";

            if (fizzCount == 3) {
                output += "Fizz";
                fizzCount = 0;
            }
            if (buzzCount == 5) {
                output += "Buzz";
                buzzCount = 0;
            }
            if (output.isEmpty()) {
                output = String.valueOf(i);
            }

            result.add(output);
        }

        return result;
    }

    // Approach 4: Hash map approach for extensibility - O(n) time, O(1) extra space
    public List<String> fizzBuzzHashMap(int n) {
        Map<Integer, String> mappings = new LinkedHashMap<>();
        mappings.put(3, "Fizz");
        mappings.put(5, "Buzz");

        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            StringBuilder output = new StringBuilder();

            for (Map.Entry<Integer, String> entry : mappings.entrySet()) {
                if (i % entry.getKey() == 0) {
                    output.append(entry.getValue());
                }
            }

            if (output.length() == 0) {
                output.append(i);
            }

            result.add(output.toString());
        }

        return result;
    }

    // Follow-up 1: Configurable rules approach
    public List<String> fizzBuzzConfigurable(int n, Map<Integer, String> rules) {
        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            final int current = i;
            StringBuilder output = new StringBuilder();

            // Apply rules in sorted order for consistency
            rules.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        if (current % entry.getKey() == 0) {
                            output.append(entry.getValue());
                        }
                    });

            if (output.length() == 0) {
                output.append(current);
            }

            result.add(output.toString());
        }

        return result;
    }

    // Follow-up 2: Without modulo operator (using bit manipulation for powers of 2)
    public List<String> fizzBuzzNoModulo(int n) {
        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            boolean divisibleBy3 = isDivisibleBy3(i);
            boolean divisibleBy5 = isDivisibleBy5(i);

            if (divisibleBy3 && divisibleBy5) {
                result.add("FizzBuzz");
            } else if (divisibleBy3) {
                result.add("Fizz");
            } else if (divisibleBy5) {
                result.add("Buzz");
            } else {
                result.add(String.valueOf(i));
            }
        }

        return result;
    }

    // Follow-up 3: Rule-based system with priority
    public static class FizzBuzzRule {
        int divisor;
        String text;
        int priority;

        public FizzBuzzRule(int divisor, String text, int priority) {
            this.divisor = divisor;
            this.text = text;
            this.priority = priority;
        }
    }

    public List<String> fizzBuzzRuleBased(int n, List<FizzBuzzRule> rules) {
        List<String> result = new ArrayList<>(n);

        // Sort rules by priority
        rules.sort(Comparator.comparingInt(r -> r.priority));

        for (int i = 1; i <= n; i++) {
            StringBuilder output = new StringBuilder();

            for (FizzBuzzRule rule : rules) {
                if (i % rule.divisor == 0) {
                    output.append(rule.text);
                }
            }

            if (output.length() == 0) {
                output.append(i);
            }

            result.add(output.toString());
        }

        return result;
    }

    // Follow-up 4: Optimized for large n (memory efficient)
    public void fizzBuzzLargeOptimized(int n, java.util.function.Consumer<String> consumer) {
        for (int i = 1; i <= n; i++) {
            if (i % 15 == 0) {
                consumer.accept("FizzBuzz");
            } else if (i % 3 == 0) {
                consumer.accept("Fizz");
            } else if (i % 5 == 0) {
                consumer.accept("Buzz");
            } else {
                consumer.accept(String.valueOf(i));
            }
        }
    }

    // Follow-up 5: Streaming version with Iterator
    public static class FizzBuzzIterator implements Iterator<String> {
        private int current;
        private final int max;
        private final Map<Integer, String> rules;

        public FizzBuzzIterator(int max) {
            this(max, createDefaultRules());
        }

        public FizzBuzzIterator(int max, Map<Integer, String> rules) {
            this.current = 1;
            this.max = max;
            this.rules = new LinkedHashMap<>(rules);
        }

        private static Map<Integer, String> createDefaultRules() {
            Map<Integer, String> defaultRules = new LinkedHashMap<>();
            defaultRules.put(3, "Fizz");
            defaultRules.put(5, "Buzz");
            return defaultRules;
        }

        @Override
        public boolean hasNext() {
            return current <= max;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            StringBuilder output = new StringBuilder();

            for (Map.Entry<Integer, String> entry : rules.entrySet()) {
                if (current % entry.getKey() == 0) {
                    output.append(entry.getValue());
                }
            }

            String result = output.length() == 0 ? String.valueOf(current) : output.toString();
            current++;
            return result;
        }
    }

    // Follow-up 5: Infinite streaming version
    public static class InfiniteFizzBuzzIterator implements Iterator<String> {
        private int current;
        private final Map<Integer, String> rules;

        public InfiniteFizzBuzzIterator() {
            this(createDefaultRules());
        }

        public InfiniteFizzBuzzIterator(Map<Integer, String> rules) {
            this.current = 1;
            this.rules = new LinkedHashMap<>(rules);
        }

        private static Map<Integer, String> createDefaultRules() {
            Map<Integer, String> defaultRules = new LinkedHashMap<>();
            defaultRules.put(3, "Fizz");
            defaultRules.put(5, "Buzz");
            return defaultRules;
        }

        @Override
        public boolean hasNext() {
            return true; // Infinite
        }

        @Override
        public String next() {
            StringBuilder output = new StringBuilder();

            for (Map.Entry<Integer, String> entry : rules.entrySet()) {
                if (current % entry.getKey() == 0) {
                    output.append(entry.getValue());
                }
            }

            String result = output.length() == 0 ? String.valueOf(current) : output.toString();
            current++;
            return result;
        }
    }

    // Advanced: Multi-threaded FizzBuzz
    public List<String> fizzBuzzParallel(int n) {
        String[] result = new String[n];

        // Process in parallel using streams
        java.util.stream.IntStream.rangeClosed(1, n)
                .parallel()
                .forEach(i -> {
                    if (i % 15 == 0) {
                        result[i - 1] = "FizzBuzz";
                    } else if (i % 3 == 0) {
                        result[i - 1] = "Fizz";
                    } else if (i % 5 == 0) {
                        result[i - 1] = "Buzz";
                    } else {
                        result[i - 1] = String.valueOf(i);
                    }
                });

        return Arrays.asList(result);
    }

    // Advanced: FizzBuzz with custom predicates
    public List<String> fizzBuzzPredicates(int n, List<java.util.function.Predicate<Integer>> predicates,
            List<String> labels) {
        if (predicates.size() != labels.size()) {
            throw new IllegalArgumentException("Predicates and labels must have same size");
        }

        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            StringBuilder output = new StringBuilder();

            for (int j = 0; j < predicates.size(); j++) {
                if (predicates.get(j).test(i)) {
                    output.append(labels.get(j));
                }
            }

            if (output.length() == 0) {
                output.append(i);
            }

            result.add(output.toString());
        }

        return result;
    }

    // Advanced: FizzBuzz with mathematical sequences
    public List<String> fizzBuzzSequences(int n) {
        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            StringBuilder output = new StringBuilder();

            // Fibonacci check
            if (isFibonacci(i)) {
                output.append("Fib");
            }

            // Prime check
            if (isPrime(i)) {
                output.append("Prime");
            }

            // Perfect square check
            if (isPerfectSquare(i)) {
                output.append("Square");
            }

            // Traditional FizzBuzz
            if (i % 3 == 0) {
                output.append("Fizz");
            }
            if (i % 5 == 0) {
                output.append("Buzz");
            }

            if (output.length() == 0) {
                output.append(i);
            }

            result.add(output.toString());
        }

        return result;
    }

    // Advanced: FizzBuzz with custom formatting
    public List<String> fizzBuzzFormatted(int n, String format) {
        List<String> result = new ArrayList<>(n);

        for (int i = 1; i <= n; i++) {
            String output;

            if (i % 15 == 0) {
                output = "FizzBuzz";
            } else if (i % 3 == 0) {
                output = "Fizz";
            } else if (i % 5 == 0) {
                output = "Buzz";
            } else {
                output = String.valueOf(i);
            }

            // Apply formatting
            switch (format.toLowerCase()) {
                case "upper":
                    output = output.toUpperCase();
                    break;
                case "lower":
                    output = output.toLowerCase();
                    break;
                case "reverse":
                    output = new StringBuilder(output).reverse().toString();
                    break;
                case "brackets":
                    output = "[" + output + "]";
                    break;
                case "padded":
                    output = String.format("%10s", output);
                    break;
                default:
                    break; // No formatting
            }

            result.add(output);
        }

        return result;
    }

    // Helper methods
    private boolean isDivisibleBy3(int num) {
        // Sum of digits approach
        while (num >= 10) {
            int sum = 0;
            while (num > 0) {
                sum += num % 10;
                num /= 10;
            }
            num = sum;
        }
        return num == 3 || num == 6 || num == 9;
    }

    private boolean isDivisibleBy5(int num) {
        // Last digit approach
        int lastDigit = num % 10;
        return lastDigit == 0 || lastDigit == 5;
    }

    private boolean isFibonacci(int n) {
        // Check if n is a perfect square: 5*n^2 + 4 or 5*n^2 - 4
        return isPerfectSquare(5 * n * n + 4) || isPerfectSquare(5 * n * n - 4);
    }

    private boolean isPrime(int n) {
        if (n <= 1)
            return false;
        if (n <= 3)
            return true;
        if (n % 2 == 0 || n % 3 == 0)
            return false;

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isPerfectSquare(int n) {
        if (n < 0)
            return false;
        int sqrt = (int) Math.sqrt(n);
        return sqrt * sqrt == n;
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(int n) {
        Map<String, Long> results = new HashMap<>();

        // Test simple approach
        long start = System.nanoTime();
        fizzBuzz(n);
        results.put("Simple", System.nanoTime() - start);

        // Test concat approach
        start = System.nanoTime();
        fizzBuzzConcat(n);
        results.put("Concat", System.nanoTime() - start);

        // Test counter approach
        start = System.nanoTime();
        fizzBuzzCounter(n);
        results.put("Counter", System.nanoTime() - start);

        // Test hashmap approach
        start = System.nanoTime();
        fizzBuzzHashMap(n);
        results.put("HashMap", System.nanoTime() - start);

        // Test parallel approach
        start = System.nanoTime();
        fizzBuzzParallel(n);
        results.put("Parallel", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        FizzBuzz solution = new FizzBuzz();

        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");
        int n = 15;

        List<String> result1 = solution.fizzBuzz(n);
        List<String> result2 = solution.fizzBuzzConcat(n);
        List<String> result3 = solution.fizzBuzzCounter(n);
        List<String> result4 = solution.fizzBuzzHashMap(n);

        System.out.println("Simple approach: " + result1);
        System.out.println("Concat approach: " + result2);
        System.out.println("Counter approach: " + result3);
        System.out.println("HashMap approach: " + result4);

        // Verify all approaches give same result
        boolean allSame = result1.equals(result2) && result2.equals(result3) && result3.equals(result4);
        System.out.println("All approaches consistent: " + allSame);

        // Test Case 2: Configurable rules
        System.out.println("\n=== Test Case 2: Configurable Rules ===");
        Map<Integer, String> customRules = new LinkedHashMap<>();
        customRules.put(2, "Even");
        customRules.put(3, "Fizz");
        customRules.put(7, "Lucky");

        List<String> configurable = solution.fizzBuzzConfigurable(14, customRules);
        System.out.println("Custom rules (2=Even, 3=Fizz, 7=Lucky): " + configurable);

        // Test Case 3: Rule-based system
        System.out.println("\n=== Test Case 3: Rule-based System ===");
        List<FizzBuzzRule> rules = Arrays.asList(
                new FizzBuzzRule(3, "Fizz", 1),
                new FizzBuzzRule(5, "Buzz", 2),
                new FizzBuzzRule(7, "Pop", 3));

        List<String> ruleBased = solution.fizzBuzzRuleBased(21, rules);
        System.out.println("Rule-based (3=Fizz, 5=Buzz, 7=Pop): " + ruleBased);

        // Test Case 4: Streaming version
        System.out.println("\n=== Test Case 4: Streaming Version ===");
        FizzBuzzIterator iterator = new FizzBuzzIterator(10);
        List<String> streamResult = new ArrayList<>();

        while (iterator.hasNext()) {
            streamResult.add(iterator.next());
        }

        System.out.println("Streaming version: " + streamResult);

        // Test Case 5: Infinite streaming (limited output)
        System.out.println("\n=== Test Case 5: Infinite Streaming ===");
        InfiniteFizzBuzzIterator infiniteIterator = new InfiniteFizzBuzzIterator();
        List<String> infiniteResult = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            infiniteResult.add(infiniteIterator.next());
        }

        System.out.println("Infinite streaming (first 15): " + infiniteResult);

        // Test Case 6: Predicate-based approach
        System.out.println("\n=== Test Case 6: Predicate-based Approach ===");
        List<java.util.function.Predicate<Integer>> predicates = Arrays.asList(
                x -> x % 2 == 0, // Even
                x -> x % 3 == 0, // Divisible by 3
                x -> x % 5 == 0 // Divisible by 5
        );
        List<String> labels = Arrays.asList("Even", "Tri", "Pent");

        List<String> predicateResult = solution.fizzBuzzPredicates(10, predicates, labels);
        System.out.println("Predicate-based: " + predicateResult);

        // Test Case 7: Mathematical sequences
        System.out.println("\n=== Test Case 7: Mathematical Sequences ===");
        List<String> sequences = solution.fizzBuzzSequences(20);
        System.out.println("With Fibonacci, Prime, Square checks: " + sequences);

        // Test Case 8: Formatted output
        System.out.println("\n=== Test Case 8: Formatted Output ===");
        System.out.println("Upper: " + solution.fizzBuzzFormatted(10, "upper"));
        System.out.println("Lower: " + solution.fizzBuzzFormatted(10, "lower"));
        System.out.println("Brackets: " + solution.fizzBuzzFormatted(10, "brackets"));
        System.out.println("Reverse: " + solution.fizzBuzzFormatted(10, "reverse"));

        // Test Case 9: Large n optimized
        System.out.println("\n=== Test Case 9: Large N Optimized ===");
        List<String> largeResult = new ArrayList<>();
        solution.fizzBuzzLargeOptimized(20, largeResult::add);
        System.out.println("Large n optimized: " + largeResult);

        // Test Case 10: Without modulo
        System.out.println("\n=== Test Case 10: Without Modulo ===");
        List<String> noModulo = solution.fizzBuzzNoModulo(20);
        System.out.println("Without modulo: " + noModulo);

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        Map<String, Long> performance = solution.comparePerformance(10000);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        System.out.println("n=1: " + solution.fizzBuzz(1));
        System.out.println("n=3: " + solution.fizzBuzz(3));
        System.out.println("n=5: " + solution.fizzBuzz(5));
        System.out.println("n=15: " + solution.fizzBuzz(15));
        System.out.println("n=100: size=" + solution.fizzBuzz(100).size());

        // Memory efficiency test
        System.out.println("\n=== Memory Efficiency Test ===");

        // Count memory usage for different approaches
        Runtime runtime = Runtime.getRuntime();

        runtime.gc();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();

        List<String> memoryTest = solution.fizzBuzz(10000);

        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        long memUsed = memAfter - memBefore;

        System.out.println("Memory used for n=10000: " + memUsed / 1024 + " KB");
        System.out.println("Result size: " + memoryTest.size());

        // Custom iterator with different rules
        System.out.println("\n=== Custom Iterator Rules ===");
        Map<Integer, String> customIteratorRules = new LinkedHashMap<>();
        customIteratorRules.put(2, "Two");
        customIteratorRules.put(4, "Four");
        customIteratorRules.put(8, "Eight");

        FizzBuzzIterator customIterator = new FizzBuzzIterator(16, customIteratorRules);
        List<String> customIteratorResult = new ArrayList<>();

        while (customIterator.hasNext()) {
            customIteratorResult.add(customIterator.next());
        }

        System.out.println("Custom iterator (powers of 2): " + customIteratorResult);

        System.out.println("\nFizz Buzz testing completed successfully!");
    }
}
