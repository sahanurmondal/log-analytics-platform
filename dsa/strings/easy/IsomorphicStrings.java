package strings.easy;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 205: Isomorphic Strings
 *
 * Given two strings s and t, determine if they are isomorphic.
 * Two strings s and t are isomorphic if the characters in s can be replaced to get t.
 *
 * All occurrences of a character must be replaced with another character while preserving
 * the order of characters. No two characters may map to the same character, but a character
 * may map to itself.
 *
 * Example 1:
 * Input: s = "egg", t = "add"
 * Output: true
 * Explanation: 'e' -> 'a', 'g' -> 'd'
 *
 * Example 2:
 * Input: s = "badc", t = "baba"
 * Output: false
 */
public class IsomorphicStrings {

    /**
     * Solution: Two Hash Maps
     * Time: O(n), Space: O(1) - max 26 letters
     *
     * We maintain two mappings:
     * 1. sMap: character in s -> character in t
     * 2. tMap: character in t -> character in s
     *
     * This ensures bijective mapping (one-to-one relationship)
     */
    public boolean isIsomorphic(String s, String t) {
        if (s.length() != t.length()) return false;

        Map<Character, Character> sMap = new HashMap<>();
        Map<Character, Character> tMap = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            char charS = s.charAt(i);
            char charT = t.charAt(i);

            // Check mapping in s -> t
            if (sMap.containsKey(charS)) {
                if (sMap.get(charS) != charT) {
                    return false;
                }
            } else {
                sMap.put(charS, charT);
            }

            // Check mapping in t -> s (ensure one-to-one)
            if (tMap.containsKey(charT)) {
                if (tMap.get(charT) != charS) {
                    return false;
                }
            } else {
                tMap.put(charT, charS);
            }
        }

        return true;
    }

    /**
     * Alternative: Single Map with Array Transform
     * Time: O(n), Space: O(1)
     */
    public boolean isIsomorphicV2(String s, String t) {
        return transform(s).equals(transform(t));
    }

    private String transform(String s) {
        Map<Character, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int id = 0;

        for (char c : s.toCharArray()) {
            if (!map.containsKey(c)) {
                map.put(c, id++);
            }
            sb.append(map.get(c)).append("#");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        IsomorphicStrings solution = new IsomorphicStrings();

        // Test case 1
        System.out.println(solution.isIsomorphic("egg", "add")); // true

        // Test case 2
        System.out.println(solution.isIsomorphic("badc", "baba")); // false

        // Test case 3
        System.out.println(solution.isIsomorphic("aba", "xyz")); // false

        // Test case 4
        System.out.println(solution.isIsomorphic("aa", "ab")); // false
    }
}

