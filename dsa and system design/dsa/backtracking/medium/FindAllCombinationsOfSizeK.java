package backtracking.medium;

import java.util.*;

/**
 * LeetCode 77: Combinations
 * https://leetcode.com/problems/combinations/
 * 
 * Company Tags: Google, Microsoft, Amazon
 * Difficulty: Medium
 * 
 * Given two integers n and k, return all possible combinations of k numbers
 * chosen from the range [1, n].
 * 
 * You may return the answer in any order.
 * 
 * Example 1:
 * Input: n = 4, k = 2
 * Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
 * 
 * Example 2:
 * Input: n = 1, k = 1
 * Output: [[1]]
 * 
 * Constraints:
 * - 1 <= n <= 20
 * - 1 <= k <= n
 */
public class FindAllCombinationsOfSizeK {

    /**
     * Approach 1: Backtracking
     * Time: O(C(n,k) * k) = O(n! / ((n-k)! * k!) * k)
     * Space: O(k) for recursion depth + O(C(n,k) * k) for result
     */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), 1, n, k);
        return result;
    }

    private void backtrack(List<List<Integer>> result, List<Integer> current,
            int start, int n, int k) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Optimization: if not enough numbers left, return early
        int needed = k - current.size();
        int available = n - start + 1;
        if (available < needed)
            return;

        for (int i = start; i <= n; i++) {
            current.add(i);
            backtrack(result, current, i + 1, n, k);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Approach 2: Iterative with Queue
     * Time: O(C(n,k) * k)
     * Space: O(C(n,k) * k)
     */
    public List<List<Integer>> combineIterative(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();

        // Start with single numbers
        for (int i = 1; i <= n; i++) {
            List<Integer> initial = new ArrayList<>();
            initial.add(i);
            queue.offer(initial);
        }

        while (!queue.isEmpty()) {
            List<Integer> current = queue.poll();

            if (current.size() == k) {
                result.add(current);
                continue;
            }

            int lastNum = current.get(current.size() - 1);
            for (int i = lastNum + 1; i <= n; i++) {
                List<Integer> next = new ArrayList<>(current);
                next.add(i);
                queue.offer(next);
            }
        }

        return result;
    }

    /**
     * Approach 3: Lexicographic (Next Permutation style)
     * Time: O(C(n,k) * k)
     * Space: O(k)
     */
    public List<List<Integer>> combineLexicographic(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();

        // Start with the first combination [1, 2, ..., k]
        List<Integer> current = new ArrayList<>();
        for (int i = 1; i <= k; i++) {
            current.add(i);
        }

        while (true) {
            result.add(new ArrayList<>(current));

            // Find the rightmost element that can be incremented
            int i = k - 1;
            while (i >= 0 && current.get(i) == n - k + i + 1) {
                i--;
            }

            if (i < 0)
                break; // No more combinations

            // Increment this element
            current.set(i, current.get(i) + 1);

            // Set all elements to the right
            for (int j = i + 1; j < k; j++) {
                current.set(j, current.get(i) + j - i);
            }
        }

        return result;
    }

    /**
     * Approach 4: Binary representation
     * Time: O(2^n * k)
     * Space: O(k)
     */
    public List<List<Integer>> combineBinary(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();

        // Check all possible subsets using bit manipulation
        for (int mask = 0; mask < (1 << n); mask++) {
            if (Integer.bitCount(mask) == k) {
                List<Integer> combination = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) {
                        combination.add(i + 1);
                    }
                }
                result.add(combination);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindAllCombinationsOfSizeK solution = new FindAllCombinationsOfSizeK();

        // Test Case 1: Basic example
        System.out.println("Test Case 1: n=4, k=2");
        List<List<Integer>> result1 = solution.combine(4, 2);
        System.out.println("Result: " + result1);
        System.out.println("Expected: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]");
        System.out.println("Count: " + result1.size() + " (Expected: 6)");

        // Test Case 2: Single element
        System.out.println("\nTest Case 2: n=1, k=1");
        List<List<Integer>> result2 = solution.combine(1, 1);
        System.out.println("Result: " + result2);
        System.out.println("Expected: [[1]]");

        // Test Case 3: All elements
        System.out.println("\nTest Case 3: n=3, k=3");
        List<List<Integer>> result3 = solution.combine(3, 3);
        System.out.println("Result: " + result3);
        System.out.println("Expected: [[1,2,3]]");

        // Test Case 4: Compare approaches
        System.out.println("\nTest Case 4: Compare approaches for n=5, k=3");
        int n = 5, k = 3;

        long start = System.nanoTime();
        List<List<Integer>> backtrack = solution.combine(n, k);
        long backtrackTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<List<Integer>> iterative = solution.combineIterative(n, k);
        long iterativeTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<List<Integer>> lexicographic = solution.combineLexicographic(n, k);
        long lexTime = System.nanoTime() - start;

        System.out.println("Backtrack count: " + backtrack.size());
        System.out.println("Iterative count: " + iterative.size());
        System.out.println("Lexicographic count: " + lexicographic.size());
        System.out.println("All equal: " + (backtrack.size() == iterative.size() &&
                iterative.size() == lexicographic.size()));

        System.out.println("Backtrack time: " + backtrackTime / 1_000_000.0 + " ms");
        System.out.println("Iterative time: " + iterativeTime / 1_000_000.0 + " ms");
        System.out.println("Lexicographic time: " + lexTime / 1_000_000.0 + " ms");

        // Test Case 5: Large combination
        System.out.println("\nTest Case 5: n=10, k=5");
        start = System.nanoTime();
        List<List<Integer>> result5 = solution.combine(10, 5);
        long time5 = System.nanoTime() - start;
        System.out.println("Count: " + result5.size() + " (Expected: 252)");
        System.out.println("Time: " + time5 / 1_000_000.0 + " ms");

        // Test Case 6: Binary approach
        System.out.println("\nTest Case 6: Binary approach for n=4, k=2");
        List<List<Integer>> binary = solution.combineBinary(4, 2);
        System.out.println("Binary result count: " + binary.size());
        System.out.println("Matches backtrack: " + (binary.size() == result1.size()));

        // Test Case 7: Edge case k=1
        System.out.println("\nTest Case 7: n=5, k=1");
        List<List<Integer>> result7 = solution.combine(5, 1);
        System.out.println("Result: " + result7);
        System.out.println("Count: " + result7.size() + " (Expected: 5)");

        // Test Case 8: Performance test
        System.out.println("\nTest Case 8: Performance test n=15, k=7");
        start = System.nanoTime();
        List<List<Integer>> perf = solution.combine(15, 7);
        long perfTime = System.nanoTime() - start;
        System.out.println("Performance count: " + perf.size() + " (Expected: 6435)");
        System.out.println("Performance time: " + perfTime / 1_000_000.0 + " ms");

        // Test Case 9: Memory usage test
        System.out.println("\nTest Case 9: Memory test");
        Runtime runtime = Runtime.getRuntime();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        solution.combine(12, 6);
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory used: " + (afterMemory - beforeMemory) / 1024.0 + " KB");

        // Test Case 10: Validation test
        System.out.println("\nTest Case 10: Validation tests");
        boolean allValidSize = result1.stream().allMatch(list -> list.size() == 2);
        boolean allInRange = result1.stream().allMatch(list -> list.stream().allMatch(num -> num >= 1 && num <= 4));
        boolean noDuplicates = result1.stream().allMatch(list -> {
            Set<Integer> set = new HashSet<>(list);
            return set.size() == list.size();
        });

        System.out.println("All combinations size " + k + ": " + allValidSize);
        System.out.println("All numbers in range [1," + 4 + "]: " + allInRange);
        System.out.println("No duplicates in combinations: " + noDuplicates);

        // Test Cases 11-15: Additional comprehensive testing
        System.out.println("\nTest Case 11: Mathematical validation C(4,2) = 6");
        int expected = factorial(4) / (factorial(2) * factorial(2));
        System.out.println("Mathematical result: " + expected);
        System.out.println("Actual result: " + result1.size());
        System.out.println("Match: " + (expected == result1.size()));

        System.out.println("\nTest Case 12: Uniqueness test");
        Set<List<Integer>> uniqueSet = new HashSet<>(result1);
        System.out.println("All unique: " + (uniqueSet.size() == result1.size()));

        System.out.println("\nTest Case 13: Sorted order test");
        boolean sorted = result1.stream().allMatch(list -> {
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i) <= list.get(i - 1))
                    return false;
            }
            return true;
        });
        System.out.println("All combinations sorted: " + sorted);

        System.out.println("\nTest Case 14: Stress test n=18, k=9");
        start = System.nanoTime();
        List<List<Integer>> stress = solution.combine(18, 9);
        long stressTime = System.nanoTime() - start;
        System.out.println("Stress count: " + stress.size());
        System.out.println("Stress time: " + stressTime / 1_000_000.0 + " ms");

        System.out.println("\nTest Case 15: Edge case n=20, k=20");
        List<List<Integer>> edge = solution.combine(20, 20);
        System.out.println("Edge result: " + edge);
        System.out.println("Count: " + edge.size() + " (Expected: 1)");
    }

    private static int factorial(int n) {
        if (n <= 1)
            return 1;
        return n * factorial(n - 1);
    }
}
