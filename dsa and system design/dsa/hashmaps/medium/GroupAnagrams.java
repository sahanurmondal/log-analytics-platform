package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 49: Group Anagrams
 * https://leetcode.com/problems/group-anagrams/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 20+ interviews)
 *
 * Description: Given an array of strings `strs`, group the anagrams together.
 * You can return the answer in any order.
 *
 * Constraints:
 * - 1 <= strs.length <= 10^4
 * - 0 <= strs[i].length <= 100
 * - strs[i] consists of lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. What if the strings contain Unicode characters?
 * 2. How would the choice of key (sorted string vs. character count) affect
 * performance?
 * 3. Can you solve this without using a HashMap?
 */
public class GroupAnagrams {

    // Approach 1: Categorize by Sorted String - O(N * K log K) time, O(N*K) space
    public List<List<String>> groupAnagrams(String[] strs) {
        if (strs == null || strs.length == 0)
            return new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] ca = s.toCharArray();
            Arrays.sort(ca);
            String key = String.valueOf(ca);
            if (!map.containsKey(key))
                map.put(key, new ArrayList<>());
            map.get(key).add(s);
        }
        return new ArrayList<>(map.values());
    }

    // Approach 2: Categorize by Character Count - O(N * K) time, O(N*K) space
    public List<List<String>> groupAnagramsByCount(String[] strs) {
        if (strs == null || strs.length == 0)
            return new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            int[] count = new int[26];
            for (char c : s.toCharArray()) {
                count[c - 'a']++;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append('#');
                sb.append(count[i]);
            }
            String key = sb.toString();

            if (!map.containsKey(key))
                map.put(key, new ArrayList<>());
            map.get(key).add(s);
        }
        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        GroupAnagrams solution = new GroupAnagrams();

        // Test case 1
        String[] strs1 = { "eat", "tea", "tan", "ate", "nat", "bat" };
        System.out.println("Result 1 (Sorted): " + solution.groupAnagrams(strs1));
        System.out.println("Result 1 (Count): " + solution.groupAnagramsByCount(strs1));

        // Test case 2: Empty strings
        String[] strs2 = { "", "" };
        System.out.println("Result 2: " + solution.groupAnagrams(strs2));

        // Test case 3: Single string
        String[] strs3 = { "a" };
        System.out.println("Result 3: " + solution.groupAnagrams(strs3));

        // Test case 4: No anagrams
        String[] strs4 = { "abc", "def", "ghi" };
        System.out.println("Result 4: " + solution.groupAnagrams(strs4));
    }
}
