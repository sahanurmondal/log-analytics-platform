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

        public UnionFind() {
            parent = new HashMap<>();
            size = new HashMap<>();
        }

        public void addNode(int x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                size.put(x, 1);
            }
        }

        public int find(int x) {
            if (parent.get(x) != x) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (size.get(rootX) < size.get(rootY)) {
                    parent.put(rootX, rootY);
                    size.put(rootY, size.get(rootX) + size.get(rootY));
                } else {
                    parent.put(rootY, rootX);
                    size.put(rootX, size.get(rootX) + size.get(rootY));
                }
            }
        }

        public int getMaxSize() {
            return size.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        }
    }

    public int longestConsecutive(int[] nums) {
        if (nums.length == 0)
            return 0;

        UnionFind uf = new UnionFind();
        Set<Integer> numSet = new HashSet<>();

        for (int num : nums) {
            numSet.add(num);
            uf.addNode(num);
        }

        for (int num : numSet) {
            if (numSet.contains(num + 1)) {
                uf.union(num, num + 1);
            }
        }

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
    }
}
