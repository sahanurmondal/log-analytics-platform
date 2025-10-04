package searching.hard;

/**
 * LeetCode 644: Maximum Average Subarray II
 * https://leetcode.com/problems/maximum-average-subarray-ii/
 * 
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description: Find the contiguous subarray of length >= k that has the maximum
 * average value.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= k <= n <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you return the actual subarray?
 * 2. What if we want exactly k elements?
 * 3. Can you handle negative averages efficiently?
 */
public class MaximumAverageSubarrayII {

    // Approach 1: Binary search on answer - O(n log(max-min)) time, O(n) space
    public double findMaxAverage(int[] nums, int k) {
        double left = -10000, right = 10000;
        double eps = 1e-5;

        while (right - left > eps) {
            double mid = left + (right - left) / 2;
            if (canAchieveAverage(nums, k, mid)) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private boolean canAchieveAverage(int[] nums, int k, double target) {
        int n = nums.length;
        double[] diff = new double[n];

        // Transform array: diff[i] = nums[i] - target
        for (int i = 0; i < n; i++) {
            diff[i] = nums[i] - target;
        }

        // Use prefix sum and sliding window minimum
        double[] prefixSum = new double[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + diff[i];
        }

        double minPrev = 0;
        for (int i = k; i <= n; i++) {
            if (prefixSum[i] - minPrev >= 0) {
                return true;
            }
            minPrev = Math.min(minPrev, prefixSum[i - k + 1]);
        }

        return false;
    }

    // Approach 2: Optimized binary search - O(n log(max-min)) time, O(1) space
    public double findMaxAverageOptimized(int[] nums, int k) {
        double left = -10000, right = 10000;

        while (right - left > 1e-5) {
            double mid = left + (right - left) / 2;
            if (hasSubarrayWithAverage(nums, k, mid)) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private boolean hasSubarrayWithAverage(int[] nums, int k, double avg) {
        double sum = 0;
        for (int i = 0; i < k; i++) {
            sum += nums[i] - avg;
        }
        if (sum >= 0)
            return true;

        double prev = 0, minPrev = 0;
        for (int i = k; i < nums.length; i++) {
            sum += nums[i] - avg;
            prev += nums[i - k] - avg;
            minPrev = Math.min(minPrev, prev);
            if (sum >= minPrev)
                return true;
        }
        return false;
    }

    // Follow-up 1: Return the actual subarray
    public int[] getMaxAverageSubarray(int[] nums, int k) {
        double maxAvg = findMaxAverage(nums, k);

        // Find the subarray with this average
        for (int len = k; len <= nums.length; len++) {
            for (int i = 0; i <= nums.length - len; i++) {
                double sum = 0;
                for (int j = i; j < i + len; j++) {
                    sum += nums[j];
                }
                if (Math.abs(sum / len - maxAvg) < 1e-5) {
                    int[] result = new int[len];
                    System.arraycopy(nums, i, result, 0, len);
                    return result;
                }
            }
        }
        return new int[0];
    }

    // Follow-up 2: Exactly k elements
    public double findMaxAverageExactK(int[] nums, int k) {
        double maxSum = Double.NEGATIVE_INFINITY;

        for (int i = 0; i <= nums.length - k; i++) {
            double sum = 0;
            for (int j = i; j < i + k; j++) {
                sum += nums[j];
            }
            maxSum = Math.max(maxSum, sum);
        }

        return maxSum / k;
    }

    // Follow-up 3: Handle negative averages with precision
    public double findMaxAverageHighPrecision(int[] nums, int k) {
        double left = Integer.MIN_VALUE, right = Integer.MAX_VALUE;

        // Binary search with higher precision
        while (right - left > 1e-9) {
            double mid = left + (right - left) / 2;
            if (canAchieveAverage(nums, k, mid)) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        MaximumAverageSubarrayII solution = new MaximumAverageSubarrayII();

        // Test case 1: Basic case
        int[] nums1 = { 1, 12, -5, -6, 50, 3 };
        System.out.println("Test 1 - Basic case (k=4):");
        double result1 = solution.findMaxAverage(nums1, 4);
        System.out.printf("Expected: 12.75, Got: %.5f\n", result1);
        System.out.printf("Optimized: %.5f\n", solution.findMaxAverageOptimized(nums1, 4));

        // Test case 2: All negative numbers
        int[] nums2 = { -1, -2, -3, -4 };
        System.out.println("\nTest 2 - All negative (k=2):");
        double result2 = solution.findMaxAverage(nums2, 2);
        System.out.printf("Expected: -1.5, Got: %.5f\n", result2);

        // Test case 3: Single element subarray
        int[] nums3 = { 5 };
        System.out.println("\nTest 3 - Single element (k=1):");
        double result3 = solution.findMaxAverage(nums3, 1);
        System.out.printf("Expected: 5.0, Got: %.5f\n", result3);

        // Test case 4: k equals array length
        int[] nums4 = { 1, 2, 3, 4 };
        System.out.println("\nTest 4 - k equals length (k=4):");
        double result4 = solution.findMaxAverage(nums4, 4);
        System.out.printf("Expected: 2.5, Got: %.5f\n", result4);

        // Test case 5: Large differences
        int[] nums5 = { -10000, 10000, -10000, 10000 };
        System.out.println("\nTest 5 - Large differences (k=2):");
        double result5 = solution.findMaxAverage(nums5, 2);
        System.out.printf("Expected: 0.0, Got: %.5f\n", result5);

        // Edge case: Two elements
        int[] nums6 = { 1, 2 };
        System.out.println("\nEdge case - Two elements (k=1):");
        double result6 = solution.findMaxAverage(nums6, 1);
        System.out.printf("Expected: 2.0, Got: %.5f\n", result6);

        // Follow-up 1: Get actual subarray
        System.out.println("\nFollow-up 1 - Actual subarray:");
        int[] subarray = solution.getMaxAverageSubarray(nums1, 4);
        System.out.println("Subarray: " + java.util.Arrays.toString(subarray));

        // Follow-up 2: Exactly k elements
        System.out.println("\nFollow-up 2 - Exactly k elements (k=3):");
        double exactK = solution.findMaxAverageExactK(nums1, 3);
        System.out.printf("Got: %.5f\n", exactK);

        // Follow-up 3: High precision
        System.out.println("\nFollow-up 3 - High precision:");
        double highPrecision = solution.findMaxAverageHighPrecision(nums1, 4);
        System.out.printf("High precision result: %.9f\n", highPrecision);

        // Performance test
        int[] largeNums = new int[10000];
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = rand.nextInt(20000) - 10000;
        }
        long startTime = System.currentTimeMillis();
        solution.findMaxAverageOptimized(largeNums, 100);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (10k elements, k=100): " + (endTime - startTime) + "ms");
    }
}
