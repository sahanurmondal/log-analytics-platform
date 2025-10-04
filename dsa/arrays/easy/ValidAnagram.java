package arrays.easy;

import java.util.*;

/**
 * LeetCode 242: Valid Anagram
 * https://leetcode.com/problems/valid-anagram/
 */
public class ValidAnagram {
    // Main solution - Character count array
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length())
            return false;

        int[] count = new int[26];

        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
            count[t.charAt(i) - 'a']--;
        }

        for (int c : count) {
            if (c != 0)
                return false;
        }

        return true;
    }

    // Alternative solution - Sorting
    public boolean isAnagramSort(String s, String t) {
        if (s.length() != t.length())
            return false;

        char[] sArray = s.toCharArray();
        char[] tArray = t.toCharArray();

        Arrays.sort(sArray);
        Arrays.sort(tArray);

        return Arrays.equals(sArray, tArray);
    }

    // Alternative solution - HashMap
    public boolean isAnagramHashMap(String s, String t) {
        if (s.length() != t.length())
            return false;

        Map<Character, Integer> count = new HashMap<>();

        for (char c : s.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }

        for (char c : t.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) - 1);
            if (count.get(c) == 0) {
                count.remove(c);
            }
        }

        return count.isEmpty();
    }

    public static void main(String[] args) {
        ValidAnagram solution = new ValidAnagram();
        System.out.println(solution.isAnagram("anagram", "nagaram")); // true
        System.out.println(solution.isAnagram("rat", "car")); // false
        System.out.println(solution.isAnagram("listen", "silent")); // true
    }
}
