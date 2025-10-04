package dp.linear.sequence;

/**
 * LeetCode 152: Maximum Product Subarray
 * https://leetcode.com/problems/maximum-product-subarray/
 *
 * Description:
 * Given an integer array nums, find a contiguous non-empty subarray within the
 * array that has the largest product,
 * and return the product.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -10 <= nums[i] <= 10
 * - The product of any prefix or suffix of nums is guaranteed to fit in a
 * 32-bit integer.
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - What if zeros are present in the array?
 * 
 * Company Tags: Facebook, Amazon, Microsoft, Google, Apple, Bloomberg, Adobe
 * Difficulty: Medium
 */
public class MaximumProductSubarray {

    // Approach 1: Brute Force - O(n^2) time, O(1) space
    public int maxProductBruteForce(int[] nums) {
        int n = nums.length;
        int maxProduct = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            int currentProduct = 1;
            for (int j = i; j < n; j++) {
                currentProduct *= nums[j];
                maxProduct = Math.max(maxProduct, currentProduct);
            }
        }

        return maxProduct;
    }

    // Approach 2: Track Min and Max (Key Insight) - O(n) time, O(1) space
    public int maxProductOptimal(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        int minEndingHere = nums[0];

        for (int i = 1; i < nums.length; i++) {
            int temp = maxEndingHere;

            // Current element can start new subarray or extend existing ones
            maxEndingHere = Math.max(nums[i], Math.max(maxEndingHere * nums[i], minEndingHere * nums[i]));
            minEndingHere = Math.min(nums[i], Math.min(temp * nums[i], minEndingHere * nums[i]));

            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }

        return maxSoFar;
    }

    // Approach 3: DP with Arrays - O(n) time, O(n) space
    public int maxProductDP(int[] nums) {
        int n = nums.length;
        int[] maxDP = new int[n];
        int[] minDP = new int[n];

        maxDP[0] = nums[0];
        minDP[0] = nums[0];
        int result = nums[0];

        for (int i = 1; i < n; i++) {
            maxDP[i] = Math.max(nums[i], Math.max(maxDP[i - 1] * nums[i], minDP[i - 1] * nums[i]));
            minDP[i] = Math.min(nums[i], Math.min(maxDP[i - 1] * nums[i], minDP[i - 1] * nums[i]));
            result = Math.max(result, maxDP[i]);
        }

        return result;
    }

    // Approach 4: Two Pass (Left to Right, Right to Left) - O(n) time, O(1) space
    public int maxProductTwoPass(int[] nums) {
        int n = nums.length;
        int maxProduct = Integer.MIN_VALUE;

        // Left to right pass
        int product = 1;
        for (int i = 0; i < n; i++) {
            product *= nums[i];
            maxProduct = Math.max(maxProduct, product);
            if (product == 0)
                product = 1; // Reset after zero
        }

        // Right to left pass
        product = 1;
        for (int i = n - 1; i >= 0; i--) {
            product *= nums[i];
            maxProduct = Math.max(maxProduct, product);
            if (product == 0)
                product = 1; // Reset after zero
        }

        return maxProduct;
    }

    // Approach 5: Handle Zeros Explicitly - O(n) time, O(1) space
    public int maxProductWithZeros(int[] nums) {
        if (nums.length == 0)
            return 0;

        int maxProduct = nums[0];
        int currentMax = nums[0];
        int currentMin = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == 0) {
                currentMax = 0;
                currentMin = 0;
                maxProduct = Math.max(maxProduct, 0);
            } else {
                int tempMax = currentMax;
                currentMax = Math.max(nums[i], Math.max(currentMax * nums[i], currentMin * nums[i]));
                currentMin = Math.min(nums[i], Math.min(tempMax * nums[i], currentMin * nums[i]));
                maxProduct = Math.max(maxProduct, currentMax);
            }
        }

        return maxProduct;
    }

    public static void main(String[] args) {
        MaximumProductSubarray solution = new MaximumProductSubarray();

        System.out.println("=== Maximum Product Subarray Test Cases ===");

        // Test Case 1: Mixed positive and negative
        int[] nums1 = { 2, 3, -2, 4 };
        System.out.println("Test 1 - Array: " + java.util.Arrays.toString(nums1));
        System.out.println("Brute Force: " + solution.maxProductBruteForce(nums1));
        System.out.println("Optimal: " + solution.maxProductOptimal(nums1));
        System.out.println("DP: " + solution.maxProductDP(nums1));
        System.out.println("Two Pass: " + solution.maxProductTwoPass(nums1));
        System.out.println("Expected: 6\n");

        // Test Case 2: All negative (even count)
        int[] nums2 = { -2, 0, -1 };
        System.out.println("Test 2 - Array: " + java.util.Arrays.toString(nums2));
        System.out.println("Optimal: " + solution.maxProductOptimal(nums2));
        System.out.println("With Zeros: " + solution.maxProductWithZeros(nums2));
        System.out.println("Expected: 0\n");

        // Test Case 3: Single negative
        int[] nums3 = { -2, 3, -4 };
        System.out.println("Test 3 - Array: " + java.util.Arrays.toString(nums3));
        System.out.println("Optimal: " + solution.maxProductOptimal(nums3));
        System.out.println("Two Pass: " + solution.maxProductTwoPass(nums3));
        System.out.println("Expected: 24\n");

        // Test Case 4: With zeros
        int[] nums4 = { -2, 0, -1, 0, 2 };
        System.out.println("Test 4 - Array: " + java.util.Arrays.toString(nums4));
        System.out.println("With Zeros: " + solution.maxProductWithZeros(nums4));
        System.out.println("Expected: 2\n");

        // Test Case 5: All positive
        int[] nums5 = { 1, 2, 3, 4 };
        System.out.println("Test 5 - Array: " + java.util.Arrays.toString(nums5));
        System.out.println("Optimal: " + solution.maxProductOptimal(nums5));
        System.out.println("Expected: 24\n");

        // Performance Test
        performanceTest();
    }

    private static void performanceTest() {
        MaximumProductSubarray solution = new MaximumProductSubarray();

        // Generate large test array
        int[] largeArray = new int[20000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 21) - 10;
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        // Test Optimal Solution
        long start = System.nanoTime();
        int result1 = solution.maxProductOptimal(largeArray);
        long end = System.nanoTime();
        System.out.println("Optimal: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        // Test Two Pass Solution
        start = System.nanoTime();
        int result2 = solution.maxProductTwoPass(largeArray);
        end = System.nanoTime();
        System.out.println("Two Pass: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
