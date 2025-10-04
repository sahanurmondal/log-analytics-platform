package strings.medium;

import java.util.*;

/**
 * LeetCode 242: Valid Anagram
 * https://leetcode.com/problems/valid-anagram/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given two strings s and t, return true if t is an anagram of s,
 * and false otherwise.
 *
 * Constraints:
 * - 1 <= s.length, t.length <= 5 * 10^4
 * - s and t consist of lowercase English letters only
 * 
 * Follow-up Questions:
 * 1. Can you handle Unicode characters?
 * 2. Can you group anagrams together?
 * 3. Can you find all anagrams in a string?
 */
public class CheckIfAnagram {

    // Approach 1: Frequency counting (O(n) time)
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

    // Follow-up 1: Handle Unicode characters
    public boolean isAnagramUnicode(String s, String t) {
        if (s.length() != t.length())
            return false;
        Map<Character, Integer> count = new HashMap<>();
        for (char c : s.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }
        for (char c : t.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) - 1);
            if (count.get(c) == 0)
                count.remove(c);
        }
        return count.isEmpty();
    }

    // Follow-up 2: Group anagrams together
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

    // Follow-up 3: Find all anagrams in a string
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length())
            return result;

        int[] pCount = new int[26], sCount = new int[26];
        for (char c : p.toCharArray())
            pCount[c - 'a']++;

        for (int i = 0; i < s.length(); i++) {
            sCount[s.charAt(i) - 'a']++;
            if (i >= p.length())
                sCount[s.charAt(i - p.length()) - 'a']--;
            if (i >= p.length() - 1 && Arrays.equals(pCount, sCount)) {
                result.add(i - p.length() + 1);
            }
        }
        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CheckIfAnagram solution = new CheckIfAnagram();

        // Test case 1: Basic case
        String s1 = "anagram", t1 = "nagaram";
        System.out.println("Test 1 - s: " + s1 + ", t: " + t1 + " Expected: true");
        System.out.println("Result: " + solution.isAnagram(s1, t1));

        // Test case 2: Unicode characters
        String s2 = "café", t2 = "facé";
        System.out.println("\nTest 2 - Unicode:");
        System.out.println("Result: " + solution.isAnagramUnicode(s2, t2));

        // Test case 3: Group anagrams
        String[] strs = { "eat", "tea", "tan", "ate", "nat", "bat" };
        System.out.println("\nTest 3 - Group anagrams:");
        System.out.println(solution.groupAnagrams(strs));

        // Test case 4: Find anagrams in string
        String s3 = "abab", p3 = "ab";
        System.out.println("\nTest 4 - Find anagrams:");
        System.out.println(solution.findAnagrams(s3, p3));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Different lengths: " + solution.isAnagram("abc", "abcd"));
        System.out.println("Empty strings: " + solution.isAnagram("", ""));
        System.out.println("Single char: " + solution.isAnagram("a", "a"));
        System.out.println("Same string: " + solution.isAnagram("hello", "hello"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb1.append((char) ('a' + (i % 26)));
            sb2.append((char) ('a' + ((25 - i) % 26)));
        }
        long start = System.nanoTime();
        boolean result = solution.isAnagram(sb1.toString(), sb2.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
