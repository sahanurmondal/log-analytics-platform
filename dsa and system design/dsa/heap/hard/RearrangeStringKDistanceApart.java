package heap.hard;

import java.util.*;

/**
 * LeetCode 358: Rearrange String k Distance Apart
 * https://leetcode.com/problems/rearrange-string-k-distance-apart/
 * 
 * Companies: Google, Facebook, Amazon
 * Frequency: Hard
 *
 * Description:
 * Given a string `s` and an integer `k`, rearrange `s` such that the same
 * characters are at least `k` distance apart.
 * If it is not possible to rearrange the string, return an empty string `""`.
 *
 * Constraints:
 * - 1 <= s.length <= 3 * 10^5
 * - s consists of only lowercase English letters.
 * - 0 <= k <= s.length
 * 
 * Follow-up Questions:
 * 1. Why is a greedy approach (always picking the most frequent character)
 * optimal?
 * 2. How does the wait queue help enforce the `k`-distance constraint?
 * 3. What is the time complexity of this solution?
 */
public class RearrangeStringKDistanceApart {

    // Approach 1: Greedy with Max-Heap and Wait Queue - O(n log 26) -> O(n) time,
    // O(26) -> O(1) space
    public String rearrangeString(String s, int k) {
        if (k == 0) {
            return s;
        }

        // 1. Count character frequencies
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // 2. Use a max-heap to greedily pick the most frequent character
        PriorityQueue<Map.Entry<Character, Integer>> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue());
        maxHeap.addAll(freqMap.entrySet());

        // 3. Use a wait queue to hold characters that cannot be used yet
        Queue<Map.Entry<Character, Integer>> waitQueue = new LinkedList<>();
        StringBuilder result = new StringBuilder();

        while (!maxHeap.isEmpty()) {
            // Get the most frequent character
            Map.Entry<Character, Integer> current = maxHeap.poll();
            result.append(current.getKey());

            // Decrement its count and add to the wait queue
            current.setValue(current.getValue() - 1);
            waitQueue.offer(current);

            // If the wait queue is full (size k), the character at the front
            // can be added back to the max-heap if its count is > 0.
            if (waitQueue.size() >= k) {
                Map.Entry<Character, Integer> released = waitQueue.poll();
                if (released.getValue() > 0) {
                    maxHeap.offer(released);
                }
            }
        }

        // If the result length is the same as the original string, it's a valid
        // rearrangement.
        return result.length() == s.length() ? result.toString() : "";
    }

    public static void main(String[] args) {
        RearrangeStringKDistanceApart solution = new RearrangeStringKDistanceApart();

        // Test case 1
        String s1 = "aabbcc";
        int k1 = 3;
        System.out.println("Rearranged 1: " + solution.rearrangeString(s1, k1)); // "abcabc" or "acbacb" etc.

        // Test case 2
        String s2 = "aaabc";
        int k2 = 3;
        System.out.println("Rearranged 2: " + solution.rearrangeString(s2, k2)); // ""

        // Test case 3
        String s3 = "aaadbbcc";
        int k3 = 2;
        System.out.println("Rearranged 3: " + solution.rearrangeString(s3, k3)); // "abacabcd" or similar

        // Test case 4: k = 0
        String s4 = "aabc";
        int k4 = 0;
        System.out.println("Rearranged 4: " + solution.rearrangeString(s4, k4)); // "aabc"

        // Test case 5: k = 1
        String s5 = "aabc";
        int k5 = 1;
        System.out.println("Rearranged 5: " + solution.rearrangeString(s5, k5)); // "aabc"
    }
}
