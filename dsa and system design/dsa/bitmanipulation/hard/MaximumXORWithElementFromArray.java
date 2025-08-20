package bitmanipulation.hard;

import java.util.*;

/**
 * LeetCode 1707: Maximum XOR With an Element From Array
 * https://leetcode.com/problems/maximum-xor-with-an-element-from-array/
 *
 * Description: You are given an array nums consisting of non-negative integers.
 * You are also given a queries array,
 * where queries[i] = [xi, mi]. The answer to the ith query is the maximum
 * bitwise XOR value of xi and any element of nums
 * that does not exceed mi. In other words, the answer is max(nums[j] XOR xi)
 * for all j such that nums[j] <= mi.
 * 
 * Constraints:
 * - 1 <= nums.length, queries.length <= 10^5
 * - queries[i].length == 2
 * - 0 <= nums[i], xi, mi <= 10^9
 *
 * Follow-up:
 * - Can you solve it using Trie?
 * - What about sorting and offline processing?
 * 
 * Time Complexity: O((n + q) * log(max_val))
 * Space Complexity: O(n * log(max_val))
 * 
 * Company Tags: Google
 */
public class MaximumXORWithElementFromArray {

    class TrieNode {
        TrieNode[] children = new TrieNode[2];
        int minValue = Integer.MAX_VALUE;
    }

    private TrieNode root;

    // Main optimized solution - Trie with minimum tracking
    public int[] maximizeXor(int[] nums, int[][] queries) {
        root = new TrieNode();

        // Build Trie
        for (int num : nums) {
            insert(num);
        }

        int[] result = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            result[i] = query(queries[i][0], queries[i][1]);
        }

        return result;
    }

    private void insert(int num) {
        TrieNode node = root;
        node.minValue = Math.min(node.minValue, num);

        for (int i = 30; i >= 0; i--) {
            int bit = (num >> i) & 1;
            if (node.children[bit] == null) {
                node.children[bit] = new TrieNode();
            }
            node = node.children[bit];
            node.minValue = Math.min(node.minValue, num);
        }
    }

    private int query(int x, int maxVal) {
        if (root.minValue > maxVal)
            return -1;

        TrieNode node = root;
        int result = 0;

        for (int i = 30; i >= 0; i--) {
            int bit = (x >> i) & 1;
            int toggledBit = 1 - bit;

            if (node.children[toggledBit] != null &&
                    node.children[toggledBit].minValue <= maxVal) {
                result |= (1 << i);
                node = node.children[toggledBit];
            } else {
                node = node.children[bit];
            }
        }

        return result;
    }

    // Alternative solution - Offline processing with sorting
    public int[] maximizeXorOffline(int[] nums, int[][] queries) {
        Arrays.sort(nums);

        // Create indexed queries for sorting
        int[][] indexedQueries = new int[queries.length][3];
        for (int i = 0; i < queries.length; i++) {
            indexedQueries[i] = new int[] { queries[i][0], queries[i][1], i };
        }

        // Sort queries by maxVal
        Arrays.sort(indexedQueries, (a, b) -> a[1] - b[1]);

        int[] result = new int[queries.length];
        TrieNode trieRoot = new TrieNode();
        int numIndex = 0;

        for (int[] query : indexedQueries) {
            int x = query[0], maxVal = query[1], queryIndex = query[2];

            // Insert all numbers <= maxVal into Trie
            while (numIndex < nums.length && nums[numIndex] <= maxVal) {
                insertSimple(trieRoot, nums[numIndex]);
                numIndex++;
            }

            if (numIndex == 0) {
                result[queryIndex] = -1;
            } else {
                result[queryIndex] = querySimple(trieRoot, x);
            }
        }

        return result;
    }

    private void insertSimple(TrieNode root, int num) {
        TrieNode node = root;
        for (int i = 30; i >= 0; i--) {
            int bit = (num >> i) & 1;
            if (node.children[bit] == null) {
                node.children[bit] = new TrieNode();
            }
            node = node.children[bit];
        }
    }

    private int querySimple(TrieNode root, int x) {
        TrieNode node = root;
        int result = 0;

        for (int i = 30; i >= 0; i--) {
            int bit = (x >> i) & 1;
            int toggledBit = 1 - bit;

            if (node.children[toggledBit] != null) {
                result |= (1 << i);
                node = node.children[toggledBit];
            } else {
                node = node.children[bit];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        MaximumXORWithElementFromArray solution = new MaximumXORWithElementFromArray();

        int[] result1 = solution.maximizeXor(new int[] { 0, 1, 2, 3, 4 }, new int[][] { { 3, 1 }, { 1, 3 }, { 5, 6 } });
        System.out.println(Arrays.toString(result1)); // Expected: [3,3,7]

        int[] result2 = solution.maximizeXor(new int[] { 5, 2, 4, 6, 6, 3 },
                new int[][] { { 12, 4 }, { 8, 1 }, { 6, 3 } });
        System.out.println(Arrays.toString(result2)); // Expected: [15,-1,5]
    }
}
