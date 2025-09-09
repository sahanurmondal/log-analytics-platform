package arrays.medium;

import java.util.*;

/**
 * LeetCode 49: Group Anagrams
 * https://leetcode.com/problems/group-anagrams/
 */
public class GroupAnagrams {
    // Main solution - Sorting as key
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }

        return new ArrayList<>(groups.values());
    }

    // Alternative solution - Character count as key
    public List<List<String>> groupAnagramsCharCount(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String str : strs) {
            String key = getCharCountKey(str);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }

        return new ArrayList<>(groups.values());
    }

    private String getCharCountKey(String str) {
        int[] count = new int[26];
        for (char c : str.toCharArray()) {
            count[c - 'a']++;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            sb.append('#').append(count[i]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        GroupAnagrams solution = new GroupAnagrams();
        String[] strs1 = { "eat", "tea", "tan", "ate", "nat", "bat" };
        System.out.println(solution.groupAnagrams(strs1));
        // Expected: [["bat"],["nat","tan"],["ate","eat","tea"]]

        String[] strs2 = { "" };
        System.out.println(solution.groupAnagrams(strs2));
        // Expected: [[""]]

        String[] strs3 = { "a" };
        System.out.println(solution.groupAnagrams(strs3));
        // Expected: [["a"]]
    }
}
