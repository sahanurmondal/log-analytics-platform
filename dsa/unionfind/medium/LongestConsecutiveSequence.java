package unionfind.medium;

import java.util.*;

/**
 * LeetCode 128: Longest Consecutive Sequence
 * https://leetcode.com/problems/longest-consecutive-sequence/
 *
 * Description:
 * Given an unsorted array of integers nums, return the length of the longest
 * consecutive elements sequence.
 * You must write an algorithm that runs in O(n) time.
 *
 * Constraints:
 * - 0 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 */
public class LongestConsecutiveSequence {

    class UnionFind {
        private Map<Integer, Integer> parent;
        private Map<Integer, Integer> size;
        private int maxSize;

        public UnionFind() {
            parent = new HashMap<>();
            size = new HashMap<>();
            maxSize = 0;
        }

        public void addNode(int x) {
            parent.putIfAbsent(x, x);
            size.putIfAbsent(x, 1);
            maxSize = Math.max(maxSize, 1);
        }

        public int find(int x) {
            if (!parent.containsKey(x)) return x;
            if (parent.get(x) != x) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(int x, int y) {
            if (!parent.containsKey(y)) return; // y doesn't exist

            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                int sizeX = size.get(rootX);
                int sizeY = size.get(rootY);

                // Always attach smaller to larger
                if (sizeX < sizeY) {
                    parent.put(rootX, rootY);
                    size.put(rootY, sizeX + sizeY);
                    maxSize = Math.max(maxSize, sizeX + sizeY);
                } else {
                    parent.put(rootY, rootX);
                    size.put(rootX, sizeX + sizeY);
                    maxSize = Math.max(maxSize, sizeX + sizeY);
                }
            }
        }

        public int getMaxSize() {
            return maxSize;
        }
    }

    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) return 0;

        UnionFind uf = new UnionFind();

        // Add all numbers to UF and eliminate duplicates in one pass
        Arrays.stream(nums).distinct().forEach(uf::addNode);

        // Union consecutive numbers
        Arrays.stream(nums).distinct().forEach(num -> {
            uf.union(num, num + 1); // Only check num+1 (not num-1) to avoid redundant unions
        });

        return uf.getMaxSize();
    }

    public static void main(String[] args) {
        LongestConsecutiveSequence solution = new LongestConsecutiveSequence();

        // Test case 1
        int[] nums1 = { 100, 4, 200, 1, 3, 2 };
        System.out.println(solution.longestConsecutive(nums1)); // 4

        // Test case 2
        int[] nums2 = { 0, 3, 7, 2, 5, 8, 4, 6, 0, 1 };
        System.out.println(solution.longestConsecutive(nums2)); // 9

        // Test case 3: Empty array
        int[] nums3 = {};
        System.out.println(solution.longestConsecutive(nums3)); // 0

        // Test case 4: Single element
        int[] nums4 = { 1 };
        System.out.println(solution.longestConsecutive(nums4)); // 1

        // Test case 5: All duplicates
        int[] nums5 = { 1, 1, 1, 1 };
        System.out.println(solution.longestConsecutive(nums5)); // 1
    }
}
