package hashmaps.medium;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 387: First Unique Character in a String
 * https://leetcode.com/problems/first-unique-character-in-a-string/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given a string `s`, find the first non-repeating character in it
 * and return its index. If it does not exist, return -1.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of only lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. Can you solve this in a single pass?
 * 2. How would you solve this if the input is a stream of characters?
 * 3. What if the character set is larger (e.g., Unicode)?
 */
public class FindFirstUniqueCharacter {

    // Approach 1: HashMap - Two passes - O(n) time, O(1) space (since charset is
    // fixed)
    public int firstUniqChar(String s) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < s.length(); i++) {
            if (freqMap.get(s.charAt(i)) == 1) {
                return i;
            }
        }

        return -1;
    }

    // Approach 2: Array as Frequency Map - Two passes - O(n) time, O(1) space
    public int firstUniqCharWithArray(String s) {
        int[] freq = new int[26];
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
        }

        for (int i = 0; i < s.length(); i++) {
            if (freq[s.charAt(i) - 'a'] == 1) {
                return i;
            }
        }

        return -1;
    }

    // Approach 3: One pass with index storage - O(n) time, O(1) space
    public int firstUniqCharOnePass(String s) {
        int[] charIndex = new int[26];
        Arrays.fill(charIndex, -1); // -1: not seen, -2: seen more than once

        for (int i = 0; i < s.length(); i++) {
            int idx = s.charAt(i) - 'a';
            if (charIndex[idx] == -1) {
                charIndex[idx] = i;
            } else {
                charIndex[idx] = -2;
            }
        }

        int minIndex = Integer.MAX_VALUE;
        for (int i = 0; i < 26; i++) {
            if (charIndex[i] >= 0) {
                minIndex = Math.min(minIndex, charIndex[i]);
            }
        }

        return minIndex == Integer.MAX_VALUE ? -1 : minIndex;
    }

    public static void main(String[] args) {
        FindFirstUniqueCharacter solution = new FindFirstUniqueCharacter();

        // Test case 1
        String s1 = "leetcode";
        System.out.println("First unique (Map): " + solution.firstUniqChar(s1)); // 0
        System.out.println("First unique (Array): " + solution.firstUniqCharWithArray(s1)); // 0
        System.out.println("First unique (One Pass): " + solution.firstUniqCharOnePass(s1)); // 0

        // Test case 2
        String s2 = "loveleetcode";
        System.out.println("First unique 2: " + solution.firstUniqChar(s2)); // 2

        // Test case 3
        String s3 = "aabb";
        System.out.println("First unique 3: " + solution.firstUniqChar(s3)); // -1
    }
}
