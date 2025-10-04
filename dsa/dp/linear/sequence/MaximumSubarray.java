package dp.linear.sequence;

/**
 * LeetCode 53: Maximum Subarray
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Description:
 * Given an integer array nums, find the contiguous subarray (containing at least one number) 
 * which has the largest sum and return its sum.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - If you have figured out the O(n) solution, try coding another solution using the divide and conquer approach, which is more subtle.
 * - Can you return the actual subarray instead of just the sum?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg, LinkedIn, Adobe
 * Difficulty: Medium (Easy in some contexts)
 */
public class MaximumSubarray {
    
    // Approach 1: Brute Force - O(n^3) time, O(1) space
    public int maxSubArrayBruteForce(int[] nums) {
        int n = nums.length;
        int maxSum = Integer.MIN_VALUE;
        
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                int currentSum = 0;
                for (int k = i; k <= j; k++) {
                    currentSum += nums[k];
                }
                maxSum = Math.max(maxSum, currentSum);
            }
        }
        
        return maxSum;
    }
    
    // Approach 2: Optimized Brute Force - O(n^2) time, O(1) space
    public int maxSubArrayOptimizedBruteForce(int[] nums) {
        int n = nums.length;
        int maxSum = Integer.MIN_VALUE;
        
        for (int i = 0; i < n; i++) {
            int currentSum = 0;
            for (int j = i; j < n; j++) {
                currentSum += nums[j];
                maxSum = Math.max(maxSum, currentSum);
            }
        }
        
        return maxSum;
    }
    
    // Approach 3: Kadane's Algorithm (Classic DP) - O(n) time, O(1) space
    public int maxSubArrayKadane(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        
        return maxSoFar;
    }
    
    // Approach 4: DP with Array - O(n) time, O(n) space
    public int maxSubArrayDP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];
        int maxSum = dp[0];
        
        for (int i = 1; i < n; i++) {
            dp[i] = Math.max(nums[i], dp[i-1] + nums[i]);
            maxSum = Math.max(maxSum, dp[i]);
        }
        
        return maxSum;
    }
    
    // Approach 5: Divide and Conquer - O(n log n) time, O(log n) space
    public int maxSubArrayDivideConquer(int[] nums) {
        return maxSubArrayHelper(nums, 0, nums.length - 1);
    }
    
    private int maxSubArrayHelper(int[] nums, int left, int right) {
        if (left == right) return nums[left];
        
        int mid = left + (right - left) / 2;
        
        // Maximum sum in left half
        int leftMax = maxSubArrayHelper(nums, left, mid);
        
        // Maximum sum in right half
        int rightMax = maxSubArrayHelper(nums, mid + 1, right);
        
        // Maximum sum crossing the middle
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }
        
        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }
        
        int crossSum = leftSum + rightSum;
        
        return Math.max(Math.max(leftMax, rightMax), crossSum);
    }
    
    // Bonus: Return the actual subarray indices
    public int[] maxSubArrayWithIndices(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];
        int start = 0, end = 0, tempStart = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (currentSum < 0) {
                currentSum = nums[i];
                tempStart = i;
            } else {
                currentSum += nums[i];
            }
            
            if (currentSum > maxSum) {
                maxSum = currentSum;
                start = tempStart;
                end = i;
            }
        }
        
        return new int[]{maxSum, start, end};
    }

    public static void main(String[] args) {
        MaximumSubarray solution = new MaximumSubarray();
        
        System.out.println("=== Maximum Subarray Test Cases ===");
        
        // Test Case 1: Mixed positive and negative
        int[] nums1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Test 1 - Array: " + java.util.Arrays.toString(nums1));
        System.out.println("Brute Force: " + solution.maxSubArrayOptimizedBruteForce(nums1));
        System.out.println("Kadane's: " + solution.maxSubArrayKadane(nums1));
        System.out.println("DP Array: " + solution.maxSubArrayDP(nums1));
        System.out.println("Divide & Conquer: " + solution.maxSubArrayDivideConquer(nums1));
        int[] result1 = solution.maxSubArrayWithIndices(nums1);
        System.out.println("With Indices - Sum: " + result1[0] + ", Start: " + result1[1] + ", End: " + result1[2]);
        System.out.println("Expected: 6\n");
        
        // Test Case 2: Single element
        int[] nums2 = {1};
        System.out.println("Test 2 - Array: " + java.util.Arrays.toString(nums2));
        System.out.println("Kadane's: " + solution.maxSubArrayKadane(nums2));
        System.out.println("Expected: 1\n");
        
        // Test Case 3: All negative
        int[] nums3 = {-3, -2, -1, -4};
        System.out.println("Test 3 - Array: " + java.util.Arrays.toString(nums3));
        System.out.println("Kadane's: " + solution.maxSubArrayKadane(nums3));
        System.out.println("Expected: -1\n");
        
        // Test Case 4: All positive
        int[] nums4 = {1, 2, 3, 4, 5};
        System.out.println("Test 4 - Array: " + java.util.Arrays.toString(nums4));
        System.out.println("Kadane's: " + solution.maxSubArrayKadane(nums4));
        System.out.println("Expected: 15\n");
        
        // Performance Test
        performanceTest();
    }
    
    private static void performanceTest() {
        MaximumSubarray solution = new MaximumSubarray();
        
        // Generate large test array
        int[] largeArray = new int[100000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int)(Math.random() * 200) - 100; // Random between -100 and 100
        }
        
        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");
        
        // Test Kadane's Algorithm
        long start = System.nanoTime();
        int result1 = solution.maxSubArrayKadane(largeArray);
        long end = System.nanoTime();
        System.out.println("Kadane's Algorithm: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
        
        // Test DP with Array
        start = System.nanoTime();
        int result2 = solution.maxSubArrayDP(largeArray);
        end = System.nanoTime();
        System.out.println("DP with Array: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
        
        // Test Divide and Conquer
        start = System.nanoTime();
        int result3 = solution.maxSubArrayDivideConquer(largeArray);
        end = System.nanoTime();
        System.out.println("Divide & Conquer: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
