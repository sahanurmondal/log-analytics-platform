package slidingwindow;

/**
 * LeetCode 53: Maximum Subarray (Kadane's Algorithm)
 * Find contiguous subarray with largest sum
 * Classic DP/Sliding window problem
 */
public class MaximumSubarray {
    
    // Kadane's Algorithm - Time: O(n), Space: O(1)
    public int maxSubArray(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        
        return maxSoFar;
    }
    
    // DP approach - same logic, different variable names
    public int maxSubArrayDP(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        int maxSum = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(nums[i], dp[i-1] + nums[i]);
            maxSum = Math.max(maxSum, dp[i]);
        }
        
        return maxSum;
    }
    
    // Divide and Conquer - Time: O(n log n), Space: O(log n)
    public int maxSubArrayDivideConquer(int[] nums) {
        return maxSubArrayHelper(nums, 0, nums.length - 1);
    }
    
    private int maxSubArrayHelper(int[] nums, int left, int right) {
        if (left == right) return nums[left];
        
        int mid = left + (right - left) / 2;
        
        int leftMax = maxSubArrayHelper(nums, left, mid);
        int rightMax = maxSubArrayHelper(nums, mid + 1, right);
        int crossMax = maxCrossingSum(nums, left, mid, right);
        
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }
    
    private int maxCrossingSum(int[] nums, int left, int mid, int right) {
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
        
        return leftSum + rightSum;
    }
    
    // Return the actual subarray indices
    public int[] maxSubArrayWithIndices(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        int start = 0, end = 0, tempStart = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (maxEndingHere < 0) {
                maxEndingHere = nums[i];
                tempStart = i;
            } else {
                maxEndingHere += nums[i];
            }
            
            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
            }
        }
        
        return new int[]{maxSoFar, start, end};
    }
    
    public static void main(String[] args) {
        MaximumSubarray solution = new MaximumSubarray();
        
        // Test case 1
        int[] nums1 = {-2,1,-3,4,-1,2,1,-5,4};
        System.out.println("Test 1: " + solution.maxSubArray(nums1)); // 6 ([4,-1,2,1])
        
        // Test case 2
        int[] nums2 = {1};
        System.out.println("Test 2: " + solution.maxSubArray(nums2)); // 1
        
        // Test case 3
        int[] nums3 = {5,4,-1,7,8};
        System.out.println("Test 3: " + solution.maxSubArray(nums3)); // 23
        
        // Edge cases
        int[] nums4 = {-1};
        System.out.println("All negative: " + solution.maxSubArray(nums4)); // -1
        
        int[] nums5 = {-2,-1,-3};
        System.out.println("All negative 2: " + solution.maxSubArray(nums5)); // -1
        
        // Compare approaches
        int[] test = {-2,1,-3,4,-1,2,1,-5,4};
        System.out.println("Kadane's: " + solution.maxSubArray(test));
        System.out.println("DP: " + solution.maxSubArrayDP(test));
        System.out.println("Divide & Conquer: " + solution.maxSubArrayDivideConquer(test));
        
        // Get subarray indices
        int[] result = solution.maxSubArrayWithIndices(test);
        System.out.println("Max sum: " + result[0] + ", from index " + result[1] + " to " + result[2]);
    }
}
