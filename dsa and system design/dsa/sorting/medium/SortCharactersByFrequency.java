package sorting.medium;

import java.util.*;

/**
 * LeetCode 451: Sort Characters By Frequency
 * https://leetcode.com/problems/sort-characters-by-frequency/
 *
 * Description:
 * Given a string s, sort it in decreasing order based on the frequency of the
 * characters.
 *
 * Constraints:
 * - 1 <= s.length <= 5 * 10^5
 * - s consists of uppercase and lowercase English letters and digits
 *
 * Follow-up:
 * - Can you solve it using bucket sort?
 * - Can you solve it using a priority queue?
 * - Can you handle Unicode characters?
 */
public class SortCharactersByFrequency {
    public String frequencySort(String s) {
        // Count frequency of each character
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Create list of characters and sort by frequency (descending)
        List<Character> chars = new ArrayList<>(freqMap.keySet());
        chars.sort((a, b) -> freqMap.get(b) - freqMap.get(a));

        // Build result string
        StringBuilder result = new StringBuilder();
        for (char c : chars) {
            int freq = freqMap.get(c);
            for (int i = 0; i < freq; i++) {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        SortCharactersByFrequency solution = new SortCharactersByFrequency();

        System.out.println(solution.frequencySort("tree")); // "eert" or "eetr"
        System.out.println(solution.frequencySort("cccaaa")); // "aaaccc" or "cccaaa"
        System.out.println(solution.frequencySort("Aabb")); // "bbAa" or "bbaA"

        // Edge Case: Single character
        System.out.println(solution.frequencySort("a")); // "a"

        // Edge Case: All same character
        System.out.println(solution.frequencySort("aaaa")); // "aaaa"

        // Edge Case: All different characters
        System.out.println(solution.frequencySort("abcd")); // Any permutation

        // Edge Case: Mixed case and digits
        System.out.println(solution.frequencySort("2a554442f544asfasssffffasss")); // Sorted by frequency
    }
}
