package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 454: 4Sum II
 * https://leetcode.com/problems/4sum-ii/
 * 
 * Companies: Amazon, Google, Meta, Microsoft, Apple
 * Frequency: High (Asked in 170+ interviews)
 *
 * Description:
 * Given four integer arrays nums1, nums2, nums3, and nums4 all of length n,
 * return the number of tuples (i, j, k, l) such that:
 * - 0 <= i, j, k, l < n
 * - nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0
 *
 * Constraints:
 * - n == nums1.length
 * - n == nums2.length
 * - n == nums3.length
 * - n == nums4.length
 * - 1 <= n <= 200
 * - -2^28 <= nums1[i], nums2[i], nums3[i], nums4[i] <= 2^28
 * 
 * Follow-up Questions:
 * 1. Can you return the actual tuples instead of just count?
 * 2. What if we have k arrays instead of 4?
 * 3. Can you solve it with different target sum?
 */
public class FourSumII {

    // Approach 1: HashMap Two-Group - O(n²) time, O(n²) space
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        Map<Integer, Integer> sumMap = new HashMap<>();

        // Store all possible sums of nums1 and nums2
        for (int a : nums1) {
            for (int b : nums2) {
                int sum = a + b;
                sumMap.put(sum, sumMap.getOrDefault(sum, 0) + 1);
            }
        }

        int count = 0;
        // For each sum of nums3 and nums4, check if negative exists in map
        for (int c : nums3) {
            for (int d : nums4) {
                int target = -(c + d);
                count += sumMap.getOrDefault(target, 0);
            }
        }

        return count;
    }

    // Approach 2: Brute Force - O(n⁴) time, O(1) space
    public int fourSumCountBruteForce(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        int count = 0;

        for (int a : nums1) {
            for (int b : nums2) {
                for (int c : nums3) {
                    for (int d : nums4) {
                        if (a + b + c + d == 0) {
                            count++;
                        }
                    }
                }
            }
        }

        return count;
    }

    // Follow-up 1: Return actual tuples
    public List<int[]> fourSumTuples(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        List<int[]> result = new ArrayList<>();
        Map<Integer, List<int[]>> sumMap = new HashMap<>();

        // Store all possible pairs and their sums from nums1 and nums2
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                int sum = nums1[i] + nums2[j];
                sumMap.computeIfAbsent(sum, k -> new ArrayList<>()).add(new int[] { i, j });
            }
        }

        // For each pair from nums3 and nums4, find matching pairs
        for (int k = 0; k < nums3.length; k++) {
            for (int l = 0; l < nums4.length; l++) {
                int target = -(nums3[k] + nums4[l]);

                if (sumMap.containsKey(target)) {
                    for (int[] pair : sumMap.get(target)) {
                        result.add(new int[] { pair[0], pair[1], k, l });
                    }
                }
            }
        }

        return result;
    }

    // Follow-up 2: K arrays (generalized solution)
    public int kSumCount(List<int[]> arrays, int target) {
        if (arrays.isEmpty())
            return 0;
        if (arrays.size() == 1) {
            return (int) Arrays.stream(arrays.get(0)).filter(x -> x == target).count();
        }

        return kSumHelper(arrays, 0, target);
    }

    private int kSumHelper(List<int[]> arrays, int index, int target) {
        if (index == arrays.size() - 1) {
            return (int) Arrays.stream(arrays.get(index)).filter(x -> x == target).count();
        }

        if (index == arrays.size() - 2) {
            // Base case: two arrays
            Map<Integer, Integer> map = new HashMap<>();
            for (int num : arrays.get(index)) {
                map.put(num, map.getOrDefault(num, 0) + 1);
            }

            int count = 0;
            for (int num : arrays.get(index + 1)) {
                count += map.getOrDefault(target - num, 0);
            }
            return count;
        }

        // Recursive case: split into two groups
        int mid = (index + arrays.size()) / 2;
        Map<Integer, Integer> leftSums = getAllSums(arrays, index, mid);

        int count = 0;
        for (Map.Entry<Integer, Integer> entry : leftSums.entrySet()) {
            int leftSum = entry.getKey();
            int leftCount = entry.getValue();
            int rightTarget = target - leftSum;

            int rightCount = kSumHelper(arrays, mid, rightTarget);
            count += leftCount * rightCount;
        }

        return count;
    }

    private Map<Integer, Integer> getAllSums(List<int[]> arrays, int start, int end) {
        Map<Integer, Integer> sums = new HashMap<>();
        sums.put(0, 1);

        for (int i = start; i < end; i++) {
            Map<Integer, Integer> newSums = new HashMap<>();

            for (Map.Entry<Integer, Integer> entry : sums.entrySet()) {
                int currentSum = entry.getKey();
                int currentCount = entry.getValue();

                for (int num : arrays.get(i)) {
                    int newSum = currentSum + num;
                    newSums.put(newSum, newSums.getOrDefault(newSum, 0) + currentCount);
                }
            }

            sums = newSums;
        }

        return sums;
    }

    // Follow-up 3: Different target sum
    public int fourSumCountWithTarget(int[] nums1, int[] nums2, int[] nums3, int[] nums4, int target) {
        Map<Integer, Integer> sumMap = new HashMap<>();

        // Store all possible sums of nums1 and nums2
        for (int a : nums1) {
            for (int b : nums2) {
                int sum = a + b;
                sumMap.put(sum, sumMap.getOrDefault(sum, 0) + 1);
            }
        }

        int count = 0;
        // For each sum of nums3 and nums4, check if (target - sum) exists in map
        for (int c : nums3) {
            for (int d : nums4) {
                int needed = target - (c + d);
                count += sumMap.getOrDefault(needed, 0);
            }
        }

        return count;
    }

    // Helper: Validate tuple
    private boolean isValidTuple(int[] nums1, int[] nums2, int[] nums3, int[] nums4,
            int[] tuple, int target) {
        int i = tuple[0], j = tuple[1], k = tuple[2], l = tuple[3];
        return nums1[i] + nums2[j] + nums3[k] + nums4[l] == target;
    }

    // Helper: Get sum statistics
    public Map<String, Object> getSumStatistics(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        Map<String, Object> stats = new HashMap<>();

        Map<Integer, Integer> pairSums12 = new HashMap<>();
        Map<Integer, Integer> pairSums34 = new HashMap<>();

        for (int a : nums1) {
            for (int b : nums2) {
                int sum = a + b;
                pairSums12.put(sum, pairSums12.getOrDefault(sum, 0) + 1);
            }
        }

        for (int c : nums3) {
            for (int d : nums4) {
                int sum = c + d;
                pairSums34.put(sum, pairSums34.getOrDefault(sum, 0) + 1);
            }
        }

        stats.put("uniquePairSums12", pairSums12.size());
        stats.put("uniquePairSums34", pairSums34.size());
        stats.put("totalPairSums12", pairSums12.values().stream().mapToInt(Integer::intValue).sum());
        stats.put("totalPairSums34", pairSums34.values().stream().mapToInt(Integer::intValue).sum());
        stats.put("maxPairSum12", Collections.max(pairSums12.keySet()));
        stats.put("minPairSum12", Collections.min(pairSums12.keySet()));
        stats.put("maxPairSum34", Collections.max(pairSums34.keySet()));
        stats.put("minPairSum34", Collections.min(pairSums34.keySet()));

        return stats;
    }

    public static void main(String[] args) {
        FourSumII solution = new FourSumII();

        // Test Case 1: Standard case
        int[] nums1 = { 1, 2 };
        int[] nums2 = { -2, -1 };
        int[] nums3 = { -1, 2 };
        int[] nums4 = { 0, 2 };

        int result1 = solution.fourSumCount(nums1, nums2, nums3, nums4);
        System.out.println("Test 1 - 4Sum count: " + result1); // Expected: 2

        // Verify with brute force
        int bruteForceResult = solution.fourSumCountBruteForce(nums1, nums2, nums3, nums4);
        System.out.println("Brute force verification: " + bruteForceResult);
        System.out.println("Results match: " + (result1 == bruteForceResult));

        // Test Case 2: All zeros
        int[] zeros = { 0, 0 };
        int result2 = solution.fourSumCount(zeros, zeros, zeros, zeros);
        System.out.println("Test 2 - All zeros: " + result2); // Expected: 16

        // Test Case 3: No valid combinations
        int[] nums3a = { 1, 1 };
        int[] nums3b = { 1, 1 };
        int[] nums3c = { 1, 1 };
        int[] nums3d = { 1, 1 };
        int result3 = solution.fourSumCount(nums3a, nums3b, nums3c, nums3d);
        System.out.println("Test 3 - No valid combinations: " + result3); // Expected: 0

        // Follow-up 1: Get actual tuples
        System.out.println("\nFollow-up 1 - Actual tuples:");
        List<int[]> tuples = solution.fourSumTuples(nums1, nums2, nums3, nums4);
        System.out.println("Number of tuples: " + tuples.size());
        for (int i = 0; i < tuples.size(); i++) {
            int[] tuple = tuples.get(i);
            System.out.printf("Tuple %d: [%d,%d,%d,%d] -> %d+%d+%d+%d=%d\n",
                    i + 1, tuple[0], tuple[1], tuple[2], tuple[3],
                    nums1[tuple[0]], nums2[tuple[1]], nums3[tuple[2]], nums4[tuple[3]],
                    nums1[tuple[0]] + nums2[tuple[1]] + nums3[tuple[2]] + nums4[tuple[3]]);
        }

        // Follow-up 2: K arrays (test with 3 arrays)
        System.out.println("\nFollow-up 2 - K arrays (3 arrays, target = 0):");
        List<int[]> arrays = Arrays.asList(nums1, nums2, nums3);
        int kSumResult = solution.kSumCount(arrays, 0);
        System.out.println("3Sum count: " + kSumResult);

        // Follow-up 3: Different target
        System.out.println("\nFollow-up 3 - Different target (target = 5):");
        int targetResult = solution.fourSumCountWithTarget(nums1, nums2, nums3, nums4, 5);
        System.out.println("4Sum count with target 5: " + targetResult);

        // Statistics
        System.out.println("\nSum Statistics:");
        Map<String, Object> stats = solution.getSumStatistics(nums1, nums2, nums3, nums4);
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        // Performance comparison
        System.out.println("\nPerformance comparison (for larger inputs):");
        int[] large1 = new int[50];
        int[] large2 = new int[50];
        int[] large3 = new int[50];
        int[] large4 = new int[50];

        Random random = new Random(42);
        for (int i = 0; i < 50; i++) {
            large1[i] = random.nextInt(200) - 100;
            large2[i] = random.nextInt(200) - 100;
            large3[i] = random.nextInt(200) - 100;
            large4[i] = random.nextInt(200) - 100;
        }

        long start = System.nanoTime();
        int optimizedResult = solution.fourSumCount(large1, large2, large3, large4);
        long optimizedTime = System.nanoTime() - start;

        System.out.println("Optimized result: " + optimizedResult);
        System.out.println("Optimized time: " + optimizedTime / 1_000_000.0 + " ms");
    }
}
