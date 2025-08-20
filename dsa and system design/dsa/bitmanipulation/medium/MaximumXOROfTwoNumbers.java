package bitmanipulation.medium;

import java.util.*;

/**
 * LeetCode 421: Maximum XOR of Two Numbers in an Array
 * https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/
 *
 * Description:
 * Given an integer array nums, return the maximum result of nums[i] XOR
 * nums[j], where 0 <= i <= j < n.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^5
 * - 0 <= nums[i] <= 2^31 - 1
 *
 * ASCII Art:
 * Binary Trie for XOR:
 * Numbers: [3, 10, 5, 25, 2, 8]
 * 
 * Trie structure (showing bit representation):
 * root
 * / \
 * 0 1
 * /|\ /|\
 * 0 1 ... branches for each bit position
 *
 * Follow-up:
 * - Can you solve it using a binary trie?
 * - Can you optimize for 32-bit integers?
 * - Can you extend to find all pairs with maximum XOR?
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class MaximumXOROfTwoNumbers {

    class TrieNode {
        TrieNode[] children = new TrieNode[2];
    }

    // Main optimized solution - Trie
    public int findMaximumXOR(int[] nums) {
        TrieNode root = new TrieNode();

        // Build Trie
        for (int num : nums) {
            TrieNode node = root;
            for (int i = 31; i >= 0; i--) {
                int bit = (num >> i) & 1;
                if (node.children[bit] == null) {
                    node.children[bit] = new TrieNode();
                }
                node = node.children[bit];
            }
        }

        int maxXor = 0;

        // Find maximum XOR for each number
        for (int num : nums) {
            TrieNode node = root;
            int currentXor = 0;

            for (int i = 31; i >= 0; i--) {
                int bit = (num >> i) & 1;
                int toggledBit = 1 - bit;

                if (node.children[toggledBit] != null) {
                    currentXor |= (1 << i);
                    node = node.children[toggledBit];
                } else {
                    node = node.children[bit];
                }
            }

            maxXor = Math.max(maxXor, currentXor);
        }

        return maxXor;
    }

    // Alternative solution - Bit manipulation with Set
    public int findMaximumXORSet(int[] nums) {
        int maxResult = 0;
        int mask = 0;

        for (int i = 31; i >= 0; i--) {
            mask |= (1 << i);
            Set<Integer> prefixes = new HashSet<>();

            for (int num : nums) {
                prefixes.add(num & mask);
            }

            int candidate = maxResult | (1 << i);

            for (int prefix : prefixes) {
                if (prefixes.contains(candidate ^ prefix)) {
                    maxResult = candidate;
                    break;
                }
            }
        }

        return maxResult;
    }

    public static void main(String[] args) {
        MaximumXOROfTwoNumbers solution = new MaximumXOROfTwoNumbers();

        System.out.println(solution.findMaximumXOR(new int[] { 3, 10, 5, 25, 2, 8 })); // Expected: 28
        System.out.println(solution.findMaximumXOR(new int[] { 14, 70, 53, 83, 49, 91, 36, 80, 92, 51, 66, 70 })); // Expected:
                                                                                                                   // 127
        System.out.println(solution.findMaximumXORSet(new int[] { 3, 10, 5, 25, 2, 8 })); // Expected: 28
    }
}
