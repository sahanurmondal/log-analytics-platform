package dp.medium;

import java.util.Arrays;

/**
 * LeetCode 673: Number of Longest Increasing Subsequence
 * https://leetcode.com/problems/number-of-longest-increasing-subsequence/
 *
 * Description:
 * Given an integer array nums, return the number of longest increasing
 * subsequences.
 * Notice that the sequence has to be strictly increasing.
 *
 * Constraints:
 * - 1 <= nums.length <= 2000
 * - -10^6 <= nums[i] <= 10^6
 *
 * Follow-up:
 * - Can you solve it in O(n log n) time?
 * - What if we need to find the actual subsequences?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class NumberOfLongestIncreasingSubsequence {

    // Approach 1: DP with Length and Count Arrays - O(n^2) time, O(n) space
    public int findNumberOfLIS(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return n;

        int[] lengths = new int[n];
        int[] counts = new int[n];

        Arrays.fill(lengths, 1);
        Arrays.fill(counts, 1);

        int maxLength = 1;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    if (lengths[j] + 1 > lengths[i]) {
                        lengths[i] = lengths[j] + 1;
                        counts[i] = counts[j];
                    } else if (lengths[j] + 1 == lengths[i]) {
                        counts[i] += counts[j];
                    }
                }
            }
            maxLength = Math.max(maxLength, lengths[i]);
        }

        int result = 0;
        for (int i = 0; i < n; i++) {
            if (lengths[i] == maxLength) {
                result += counts[i];
            }
        }

        return result;
    }

    // Approach 2: Segment Tree - O(n log n) time, O(n) space
    public int findNumberOfLISSegmentTree(int[] nums) {
        if (nums.length == 0)
            return 0;

        // Coordinate compression
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        SegmentTree tree = new SegmentTree(sorted.length);

        for (int num : nums) {
            int index = Arrays.binarySearch(sorted, num);
            Pair queryResult = tree.query(0, index - 1);
            tree.update(index, new Pair(queryResult.length + 1, Math.max(1, queryResult.count)));
        }

        Pair result = tree.query(0, sorted.length - 1);
        return result.count;
    }

    class Pair {
        int length, count;

        Pair(int length, int count) {
            this.length = length;
            this.count = count;
        }
    }

    class SegmentTree {
        Pair[] tree;
        int n;

        SegmentTree(int n) {
            this.n = n;
            tree = new Pair[4 * n];
            for (int i = 0; i < tree.length; i++) {
                tree[i] = new Pair(0, 1);
            }
        }

        void update(int index, Pair value) {
            update(1, 0, n - 1, index, value);
        }

        void update(int node, int start, int end, int index, Pair value) {
            if (start == end) {
                tree[node] = value;
            } else {
                int mid = (start + end) / 2;
                if (index <= mid) {
                    update(2 * node, start, mid, index, value);
                } else {
                    update(2 * node + 1, mid + 1, end, index, value);
                }

                Pair left = tree[2 * node];
                Pair right = tree[2 * node + 1];

                if (left.length > right.length) {
                    tree[node] = left;
                } else if (left.length < right.length) {
                    tree[node] = right;
                } else {
                    tree[node] = new Pair(left.length, left.count + right.count);
                }
            }
        }

        Pair query(int left, int right) {
            if (left > right)
                return new Pair(0, 1);
            return query(1, 0, n - 1, left, right);
        }

        Pair query(int node, int start, int end, int left, int right) {
            if (right < start || end < left) {
                return new Pair(0, 1);
            }

            if (left <= start && end <= right) {
                return tree[node];
            }

            int mid = (start + end) / 2;
            Pair leftResult = query(2 * node, start, mid, left, right);
            Pair rightResult = query(2 * node + 1, mid + 1, end, left, right);

            if (leftResult.length > rightResult.length) {
                return leftResult;
            } else if (leftResult.length < rightResult.length) {
                return rightResult;
            } else {
                return new Pair(leftResult.length, leftResult.count + rightResult.count);
            }
        }
    }

    // Approach 3: Coordinate Compression with TreeMap - O(n log n) time, O(n) space
    public int findNumberOfLISTreeMap(int[] nums) {
        if (nums.length == 0)
            return 0;

        java.util.TreeMap<Integer, Integer> lengthToCount = new java.util.TreeMap<>();
        lengthToCount.put(0, 1);

        for (int num : nums) {
            java.util.Map.Entry<Integer, Integer> entry = lengthToCount.lowerEntry(num);
            int length = entry == null ? 0 : entry.getKey();
            int count = entry == null ? 1 : entry.getValue();

            int newLength = length + 1;
            lengthToCount.put(newLength, lengthToCount.getOrDefault(newLength, 0) + count);

            // Remove entries with same or smaller count but greater length
            java.util.Iterator<java.util.Map.Entry<Integer, Integer>> iter = lengthToCount.tailMap(newLength + 1)
                    .entrySet().iterator();

            while (iter.hasNext()) {
                java.util.Map.Entry<Integer, Integer> next = iter.next();
                if (next.getValue() <= count) {
                    iter.remove();
                } else {
                    break;
                }
            }
        }

        return lengthToCount.lastEntry().getValue();
    }

    // Approach 4: Binary Indexed Tree - O(n log n) time, O(n) space
    public int findNumberOfLISBIT(int[] nums) {
        if (nums.length == 0)
            return 0;

        // Coordinate compression
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        BIT lengthBIT = new BIT(sorted.length);
        BIT countBIT = new BIT(sorted.length);

        for (int num : nums) {
            int index = Arrays.binarySearch(sorted, num) + 1; // 1-indexed

            int maxLength = lengthBIT.query(index - 1);
            int count = countBIT.query(index - 1);

            if (count == 0)
                count = 1;

            lengthBIT.update(index, maxLength + 1);
            countBIT.update(index, count);
        }

        int maxLength = lengthBIT.query(sorted.length);
        return countBIT.queryCount(sorted.length, maxLength);
    }

    class BIT {
        int[] lengthTree, countTree;
        int n;

        BIT(int n) {
            this.n = n;
            lengthTree = new int[n + 1];
            countTree = new int[n + 1];
        }

        void update(int index, int length) {
            for (int i = index; i <= n; i += i & (-i)) {
                if (lengthTree[i] < length) {
                    lengthTree[i] = length;
                    countTree[i] = 1;
                } else if (lengthTree[i] == length) {
                    countTree[i]++;
                }
            }
        }

        int query(int index) {
            int maxLength = 0;
            for (int i = index; i > 0; i -= i & (-i)) {
                maxLength = Math.max(maxLength, lengthTree[i]);
            }
            return maxLength;
        }

        int queryCount(int index, int targetLength) {
            int count = 0;
            for (int i = index; i > 0; i -= i & (-i)) {
                if (lengthTree[i] == targetLength) {
                    count += countTree[i];
                }
            }
            return count;
        }
    }

    // Approach 5: Patience Sorting with Binary Search - O(n log n) time, O(n) space
    public int findNumberOfLISPatience(int[] nums) {
        if (nums.length == 0)
            return 0;

        java.util.List<java.util.List<Integer>> tails = new java.util.ArrayList<>();
        java.util.List<java.util.List<Integer>> counts = new java.util.ArrayList<>();

        for (int num : nums) {
            int pos = binarySearch(tails, num);

            if (pos == tails.size()) {
                tails.add(new java.util.ArrayList<>());
                counts.add(new java.util.ArrayList<>());
            }

            tails.get(pos).add(num);

            int count = 1;
            if (pos > 0) {
                int prevPos = binarySearchCount(tails.get(pos - 1), counts.get(pos - 1), num);
                count = getSum(counts.get(pos - 1), prevPos);
            }

            counts.get(pos).add(count);
        }

        java.util.List<Integer> lastCounts = counts.get(counts.size() - 1);
        return getSum(lastCounts, lastCounts.size() - 1);
    }

    private int binarySearch(java.util.List<java.util.List<Integer>> tails, int target) {
        int left = 0, right = tails.size();

        while (left < right) {
            int mid = (left + right) / 2;
            if (tails.get(mid).get(tails.get(mid).size() - 1) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private int binarySearchCount(java.util.List<Integer> tail, java.util.List<Integer> count, int target) {
        int left = 0, right = tail.size();

        while (left < right) {
            int mid = (left + right) / 2;
            if (tail.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left - 1;
    }

    private int getSum(java.util.List<Integer> counts, int index) {
        int sum = 0;
        for (int i = 0; i <= index; i++) {
            sum += counts.get(i);
        }
        return sum;
    }

    public static void main(String[] args) {
        NumberOfLongestIncreasingSubsequence solution = new NumberOfLongestIncreasingSubsequence();

        System.out.println("=== Number of Longest Increasing Subsequence Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 1, 3, 5, 4, 7 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("DP: " + solution.findNumberOfLIS(nums1));
        System.out.println("Segment Tree: " + solution.findNumberOfLISSegmentTree(nums1));
        System.out.println("TreeMap: " + solution.findNumberOfLISTreeMap(nums1));
        System.out.println("Expected: 2\n");

        // Test Case 2: Another example
        int[] nums2 = { 2, 2, 2, 2, 2 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("DP: " + solution.findNumberOfLIS(nums2));
        System.out.println("Expected: 5\n");

        // Test Case 3: Increasing sequence
        int[] nums3 = { 1, 2, 3, 4, 5 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("DP: " + solution.findNumberOfLIS(nums3));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        NumberOfLongestIncreasingSubsequence solution = new NumberOfLongestIncreasingSubsequence();

        int[] largeArray = new int[2000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000000) - 500000;
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.findNumberOfLIS(largeArray);
        long end = System.nanoTime();
        System.out.println("DP O(n²): " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.findNumberOfLISSegmentTree(largeArray);
        end = System.nanoTime();
        System.out.println("Segment Tree O(n log n): " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
