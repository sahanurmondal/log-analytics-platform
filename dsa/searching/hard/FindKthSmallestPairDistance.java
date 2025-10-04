package searching.hard;

import java.util.*;

/**
 * LeetCode 719: Find K-th Smallest Pair Distance
 * https://leetcode.com/problems/find-k-th-smallest-pair-distance/
 * 
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description: Find the k-th smallest distance among all pairs of elements.
 *
 * Constraints:
 * - 2 <= nums.length <= 10^4
 * - 0 <= nums[i] <= 10^6
 * - 1 <= k <= n*(n-1)/2
 * 
 * Follow-up Questions:
 * 1. Can you return all pairs with distance equal to k-th smallest?
 * 2. What if we want k-th largest distance?
 * 3. Can you handle weighted pairs?
 */
public class FindKthSmallestPairDistance {

    // Approach 1: Binary search + sliding window - O(n log n + n log(max-min))
    // time, O(1) space
    public int smallestDistancePair(int[] nums, int k) {
        Arrays.sort(nums);
        int left = 0, right = nums[nums.length - 1] - nums[0];

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (countPairs(nums, mid) < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private int countPairs(int[] nums, int target) {
        int count = 0, left = 0;
        for (int right = 1; right < nums.length; right++) {
            while (nums[right] - nums[left] > target) {
                left++;
            }
            count += right - left;
        }
        return count;
    }

    // Approach 2: Heap-based solution - O(n^2 log k) time, O(k) space
    public int smallestDistancePairHeap(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int distance = Math.abs(nums[i] - nums[j]);
                maxHeap.offer(distance);
                if (maxHeap.size() > k) {
                    maxHeap.poll();
                }
            }
        }
        return maxHeap.peek();
    }

    // Approach 3: Count sort optimization - O(n^2) time, O(max-min) space
    public int smallestDistancePairCount(int[] nums, int k) {
        Arrays.sort(nums);
        int n = nums.length;
        int maxDist = nums[n - 1] - nums[0];
        int[] count = new int[maxDist + 1];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                count[nums[j] - nums[i]]++;
            }
        }

        for (int i = 0; i <= maxDist; i++) {
            k -= count[i];
            if (k <= 0)
                return i;
        }
        return -1;
    }

    // Follow-up 1: Return all pairs with k-th smallest distance
    public List<int[]> getPairsWithKthDistance(int[] nums, int k) {
        int kthDistance = smallestDistancePair(nums, k);
        List<int[]> pairs = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (Math.abs(nums[i] - nums[j]) == kthDistance) {
                    pairs.add(new int[] { nums[i], nums[j] });
                }
            }
        }
        return pairs;
    }

    // Follow-up 2: Find k-th largest distance
    public int largestDistancePair(int[] nums, int k) {
        Arrays.sort(nums);
        int left = 0, right = nums[nums.length - 1] - nums[0];
        int totalPairs = nums.length * (nums.length - 1) / 2;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (countPairsLessEqual(nums, mid) < totalPairs - k + 1) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private int countPairsLessEqual(int[] nums, int target) {
        int count = 0, left = 0;
        for (int right = 1; right < nums.length; right++) {
            while (nums[right] - nums[left] > target) {
                left++;
            }
            count += right - left;
        }
        return count;
    }

    // Follow-up 3: Handle weighted pairs
    public int smallestWeightedDistance(int[] nums, int[] weights, int k) {
        List<int[]> weightedPairs = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int distance = Math.abs(nums[i] - nums[j]);
                int weight = weights[i] + weights[j];
                weightedPairs.add(new int[] { distance, weight, i, j });
            }
        }

        weightedPairs.sort((a, b) -> {
            int cmp = Integer.compare(a[0], b[0]); // Compare by distance first
            return cmp != 0 ? cmp : Integer.compare(a[1], b[1]); // Then by weight
        });

        return weightedPairs.get(k - 1)[0];
    }

    public static void main(String[] args) {
        FindKthSmallestPairDistance solution = new FindKthSmallestPairDistance();

        // Test case 1: Basic case
        int[] nums1 = { 1, 3, 1 };
        System.out.println("Test 1 - Basic case (k=1):");
        System.out.println("Expected: 0, Got: " + solution.smallestDistancePair(nums1, 1));
        System.out.println("Heap approach: " + solution.smallestDistancePairHeap(nums1, 1));
        System.out.println("Count approach: " + solution.smallestDistancePairCount(nums1, 1));

        // Test case 2: All elements same
        int[] nums2 = { 1, 1, 1 };
        System.out.println("\nTest 2 - All same elements (k=2):");
        System.out.println("Expected: 0, Got: " + solution.smallestDistancePair(nums2, 2));

        // Test case 3: Sorted array
        int[] nums3 = { 1, 6, 1 };
        System.out.println("\nTest 3 - Mixed values (k=3):");
        System.out.println("Expected: 5, Got: " + solution.smallestDistancePair(nums3, 3));

        // Test case 4: Large distances
        int[] nums4 = { 9, 10, 7, 10, 6, 1, 5, 4, 9, 8 };
        System.out.println("\nTest 4 - Large array (k=18):");
        System.out.println("Expected: 2, Got: " + solution.smallestDistancePair(nums4, 18));

        // Test case 5: Two elements
        int[] nums5 = { 1, 5 };
        System.out.println("\nTest 5 - Two elements (k=1):");
        System.out.println("Expected: 4, Got: " + solution.smallestDistancePair(nums5, 1));

        // Edge case: Large k
        int[] nums6 = { 1, 2, 3, 4 };
        int maxK = nums6.length * (nums6.length - 1) / 2;
        System.out.println("\nEdge case - Maximum k (" + maxK + "):");
        System.out.println("Expected: 3, Got: " + solution.smallestDistancePair(nums6, maxK));

        // Follow-up 1: Get pairs with k-th distance
        System.out.println("\nFollow-up 1 - Pairs with k-th distance:");
        List<int[]> pairs = solution.getPairsWithKthDistance(nums1, 1);
        for (int[] pair : pairs) {
            System.out.println("[" + pair[0] + ", " + pair[1] + "]");
        }

        // Follow-up 2: K-th largest distance
        System.out.println("\nFollow-up 2 - K-th largest distance (k=1):");
        System.out.println("Expected: 2, Got: " + solution.largestDistancePair(nums1, 1));

        // Follow-up 3: Weighted pairs
        int[] weights = { 1, 2, 3 };
        System.out.println("\nFollow-up 3 - Weighted pairs (k=1):");
        System.out.println("Got: " + solution.smallestWeightedDistance(nums1, weights, 1));

        // Performance test
        int[] largeNums = new int[1000];
        Random rand = new Random();
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = rand.nextInt(1000);
        }
        long startTime = System.currentTimeMillis();
        solution.smallestDistancePair(largeNums, 100000);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (1000 elements): " + (endTime - startTime) + "ms");

        // Verification: Show all distances for small example
        System.out.println("\nVerification for [1,3,1]:");
        System.out.println("Pairs and distances: (1,3)=2, (1,1)=0, (3,1)=2");
        System.out.println("Sorted distances: [0, 2, 2]");
        System.out.println("1st smallest: 0, 2nd smallest: 2, 3rd smallest: 2");
    }
}
