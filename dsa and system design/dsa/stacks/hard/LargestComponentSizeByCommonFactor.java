package stacks.hard;

import java.util.*;

/**
 * LeetCode 952: Largest Component Size by Common Factor
 * https://leetcode.com/problems/largest-component-size-by-common-factor/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given an array of unique positive integers, consider the graph
 * where each integer is a node and there is an edge between two nodes if they
 * share a common factor greater than 1. Return the size of the largest
 * connected component in the graph.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - 1 <= nums[i] <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find all connected components?
 * 2. Can you handle dynamic updates to the array?
 * 3. Can you optimize for very large numbers?
 */
public class LargestComponentSizeByCommonFactor {

    // Approach 1: Union-Find with factorization
    public int largestComponentSize(int[] nums) {
        int max = Arrays.stream(nums).max().getAsInt();
        int[] parent = new int[max + 1];
        for (int i = 0; i <= max; i++)
            parent[i] = i;
        for (int num : nums) {
            for (int f = 2; f * f <= num; f++) {
                if (num % f == 0) {
                    union(parent, num, f);
                    union(parent, num, num / f);
                }
            }
        }
        Map<Integer, Integer> count = new HashMap<>();
        int res = 0;
        for (int num : nums) {
            int root = find(parent, num);
            count.put(root, count.getOrDefault(root, 0) + 1);
            res = Math.max(res, count.get(root));
        }
        return res;
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x)
            parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    private void union(int[] parent, int x, int y) {
        int px = find(parent, x), py = find(parent, y);
        if (px != py)
            parent[px] = py;
    }

    // Follow-up 1: Get all connected components
    public List<List<Integer>> getAllComponents(int[] nums) {
        int max = Arrays.stream(nums).max().getAsInt();
        int[] parent = new int[max + 1];
        for (int i = 0; i <= max; i++)
            parent[i] = i;
        for (int num : nums) {
            for (int f = 2; f * f <= num; f++) {
                if (num % f == 0) {
                    union(parent, num, f);
                    union(parent, num, num / f);
                }
            }
        }
        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int num : nums) {
            int root = find(parent, num);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(num);
        }
        return new ArrayList<>(groups.values());
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LargestComponentSizeByCommonFactor solution = new LargestComponentSizeByCommonFactor();

        // Test case 1: Basic case
        int[] nums1 = { 4, 6, 15, 35 };
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + " Expected: 4");
        System.out.println("Result: " + solution.largestComponentSize(nums1));

        // Test case 2: All components
        System.out.println("\nTest 2 - All components:");
        System.out.println(solution.getAllComponents(nums1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + solution.largestComponentSize(new int[] { 7 }));
        System.out.println("All primes: " + solution.largestComponentSize(new int[] { 2, 3, 5, 7, 11 }));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++)
            large[i] = i + 2;
        long start = System.nanoTime();
        int result = solution.largestComponentSize(large);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
