package searching.hard;

import java.util.*;

/**
 * LeetCode 315: Count of Smaller Numbers After Self
 * https://leetcode.com/problems/count-of-smaller-numbers-after-self/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given an integer array nums, return an integer array counts
 * where counts[i] is the number of smaller elements to the right of nums[i].
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you return the indices of smaller elements?
 * 2. What if we need count of larger elements?
 * 3. Can you handle range queries efficiently?
 */
public class CountSmallerAfterSelf {

    // Approach 1: Merge sort with counting - O(n log n) time, O(n) space
    public List<Integer> countSmaller(int[] nums) {
        if (nums == null || nums.length == 0)
            return new ArrayList<>();

        int n = nums.length;
        int[] indices = new int[n];
        int[] counts = new int[n];

        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        mergeSort(nums, indices, counts, 0, n - 1);

        List<Integer> result = new ArrayList<>();
        for (int count : counts) {
            result.add(count);
        }
        return result;
    }

    private void mergeSort(int[] nums, int[] indices, int[] counts, int start, int end) {
        if (start >= end)
            return;

        int mid = start + (end - start) / 2;
        mergeSort(nums, indices, counts, start, mid);
        mergeSort(nums, indices, counts, mid + 1, end);
        merge(nums, indices, counts, start, mid, end);
    }

    private void merge(int[] nums, int[] indices, int[] counts, int start, int mid, int end) {
        int[] temp = new int[end - start + 1];
        int i = start, j = mid + 1, k = 0, rightCount = 0;

        while (i <= mid && j <= end) {
            if (nums[indices[j]] < nums[indices[i]]) {
                temp[k++] = indices[j++];
                rightCount++;
            } else {
                temp[k++] = indices[i];
                counts[indices[i]] += rightCount;
                i++;
            }
        }

        while (i <= mid) {
            temp[k++] = indices[i];
            counts[indices[i]] += rightCount;
            i++;
        }

        while (j <= end) {
            temp[k++] = indices[j++];
        }

        for (i = start; i <= end; i++) {
            indices[i] = temp[i - start];
        }
    }

    // Approach 2: Binary Indexed Tree (Fenwick Tree) - O(n log k) time, O(k) space
    public List<Integer> countSmallerBIT(int[] nums) {
        if (nums == null || nums.length == 0)
            return new ArrayList<>();

        // Coordinate compression
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        Map<Integer, Integer> compress = new HashMap<>();
        int rank = 1;
        for (int num : sorted) {
            if (!compress.containsKey(num)) {
                compress.put(num, rank++);
            }
        }

        BIT bit = new BIT(rank);
        List<Integer> result = new ArrayList<>();

        for (int i = nums.length - 1; i >= 0; i--) {
            int compressedVal = compress.get(nums[i]);
            result.add(bit.query(compressedVal - 1));
            bit.update(compressedVal, 1);
        }

        Collections.reverse(result);
        return result;
    }

    class BIT {
        private int[] tree;

        public BIT(int n) {
            tree = new int[n + 1];
        }

        public void update(int i, int val) {
            while (i < tree.length) {
                tree[i] += val;
                i += i & (-i);
            }
        }

        public int query(int i) {
            int sum = 0;
            while (i > 0) {
                sum += tree[i];
                i -= i & (-i);
            }
            return sum;
        }
    }

    // Follow-up 1: Return indices of smaller elements
    public List<List<Integer>> getSmallerIndices(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            List<Integer> smallerIndices = new ArrayList<>();
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] < nums[i]) {
                    smallerIndices.add(j);
                }
            }
            result.add(smallerIndices);
        }
        return result;
    }

    // Follow-up 2: Count larger elements after self
    public List<Integer> countLarger(int[] nums) {
        if (nums == null || nums.length == 0)
            return new ArrayList<>();

        // Coordinate compression
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        Map<Integer, Integer> compress = new HashMap<>();
        int rank = 1;
        for (int num : sorted) {
            if (!compress.containsKey(num)) {
                compress.put(num, rank++);
            }
        }

        BIT bit = new BIT(rank);
        List<Integer> result = new ArrayList<>();

        for (int i = nums.length - 1; i >= 0; i--) {
            int compressedVal = compress.get(nums[i]);
            result.add(bit.query(rank - 1) - bit.query(compressedVal));
            bit.update(compressedVal, 1);
        }

        Collections.reverse(result);
        return result;
    }

    // Follow-up 3: Range query version
    public List<Integer> countSmallerInRange(int[] nums, int[][] queries) {
        List<Integer> result = new ArrayList<>();
        for (int[] query : queries) {
            int left = query[0], right = query[1], target = query[2];
            int count = 0;
            for (int i = left; i <= right; i++) {
                if (nums[i] < target)
                    count++;
            }
            result.add(count);
        }
        return result;
    }

    public static void main(String[] args) {
        CountSmallerAfterSelf solution = new CountSmallerAfterSelf();

        // Test case 1: Basic case
        int[] nums1 = { 5, 2, 6, 1 };
        System.out.println("Test 1 - Basic case:");
        System.out.println("Expected: [2, 1, 1, 0], Got: " + solution.countSmaller(nums1));
        System.out.println("BIT approach: " + solution.countSmallerBIT(nums1));

        // Test case 2: Sorted array (ascending)
        int[] nums2 = { 1, 2, 3, 4, 5 };
        System.out.println("\nTest 2 - Sorted ascending:");
        System.out.println("Expected: [0, 0, 0, 0, 0], Got: " + solution.countSmaller(nums2));

        // Test case 3: Sorted array (descending)
        int[] nums3 = { 5, 4, 3, 2, 1 };
        System.out.println("\nTest 3 - Sorted descending:");
        System.out.println("Expected: [4, 3, 2, 1, 0], Got: " + solution.countSmaller(nums3));

        // Test case 4: Duplicates
        int[] nums4 = { 2, 2, 2, 2 };
        System.out.println("\nTest 4 - All duplicates:");
        System.out.println("Expected: [0, 0, 0, 0], Got: " + solution.countSmaller(nums4));

        // Test case 5: Negative numbers
        int[] nums5 = { -1, -2, 0, 1 };
        System.out.println("\nTest 5 - Negative numbers:");
        System.out.println("Expected: [1, 0, 0, 0], Got: " + solution.countSmaller(nums5));

        // Edge case: Single element
        int[] nums6 = { 5 };
        System.out.println("\nEdge case - Single element:");
        System.out.println("Expected: [0], Got: " + solution.countSmaller(nums6));

        // Follow-up 1: Get indices of smaller elements
        System.out.println("\nFollow-up 1 - Indices of smaller elements:");
        System.out.println("For [5,2,6,1]: " + solution.getSmallerIndices(nums1));

        // Follow-up 2: Count larger elements
        System.out.println("\nFollow-up 2 - Count larger elements:");
        System.out.println("Expected: [1, 2, 0, 0], Got: " + solution.countLarger(nums1));

        // Follow-up 3: Range queries
        int[][] queries = { { 0, 2, 3 }, { 1, 3, 2 } };
        System.out.println("\nFollow-up 3 - Range queries:");
        System.out.println("Range queries result: " + solution.countSmallerInRange(nums1, queries));

        // Performance test
        int[] largeNums = new int[10000];
        Random rand = new Random();
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = rand.nextInt(20000) - 10000;
        }
        long startTime = System.currentTimeMillis();
        solution.countSmallerBIT(largeNums);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (10k elements): " + (endTime - startTime) + "ms");
    }
}
