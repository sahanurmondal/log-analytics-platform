package strings;

import java.util.*;

/**
 * LeetCode 3: Longest Substring Without Repeating Characters
 * Sliding window technique
 * Time: O(n), Space: O(min(m,n)) where m is charset size
 */
public class LongestSubstringWithoutRepeating {
    
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int maxLen = 0;
        int left = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            if (map.containsKey(c)) {
                left = Math.max(left, map.get(c) + 1);
            }
            
            map.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
    
    // Alternative: Using Set
    public int lengthOfLongestSubstringSet(String s) {
        Set<Character> set = new HashSet<>();
        int maxLen = 0;
        int left = 0;
        
        for (int right = 0; right < s.length(); right++) {
            while (set.contains(s.charAt(right))) {
                set.remove(s.charAt(left));
                left++;
            }
            set.add(s.charAt(right));
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
    
    /**
     * Approach 1: Sliding Window with HashMap
     * Steps:
     * 1. Use a HashMap to store the last index of each character seen.
     * 2. Move the right pointer through the string, updating the left pointer when a duplicate is found.
     * 3. Update the max length at each step.
     * Time Complexity: O(n) - Each character is visited at most twice (once by right pointer, once by left pointer).
     *   - Example: For input "abcabcbb", each character is added and removed from the map at most once.
     * Space Complexity: O(min(n, m)) - n is the length of the string, m is the charset size.
     *   - Example: For ASCII, m=128, so space is O(128) = O(1) for small charset; for unicode, m can be large.
     *
     * Approach 2: Sliding Window with HashSet
     * Steps:
     * 1. Use a HashSet to store unique characters in the current window.
     * 2. Expand the window by moving the right pointer; if a duplicate is found, shrink from the left.
     * 3. Update the max length at each step.
     * Time Complexity: O(n) - Each character is visited at most twice (once added, once removed from set).
     *   - Example: For input "pwwkew", 'w' is added, then removed, then added again, but never more than twice.
     * Space Complexity: O(min(n, m)) - n is the length of the string, m is the charset size.
     *   - Example: For input with all unique characters, set size grows to O(m) at most.
     */
    public static void main(String[] args) {
        LongestSubstringWithoutRepeating solution = new LongestSubstringWithoutRepeating();
        // Edge Case 1: Normal case with repeating characters
        System.out.println(solution.lengthOfLongestSubstring("abcabcbb")); // 3
        // Edge Case 2: All characters are the same
        System.out.println(solution.lengthOfLongestSubstring("bbbbb")); // 1
        // Edge Case 3: Substring in the middle
        System.out.println(solution.lengthOfLongestSubstring("pwwkew")); // 3
        // Edge Case 4: Empty string
        System.out.println(solution.lengthOfLongestSubstring("")); // 0
        // Edge Case 5: Overlapping substrings
        System.out.println(solution.lengthOfLongestSubstring("dvdf")); // 3
        // Edge Case 6: All unique characters
        System.out.println(solution.lengthOfLongestSubstring("abcdefg")); // 7
        // Edge Case 7: String with spaces and symbols
        System.out.println(solution.lengthOfLongestSubstring("a b!c@d#e$")); // 8
        // Edge Case 8: String with unicode characters
        System.out.println(solution.lengthOfLongestSubstring("a😊b😊c")); // 3
        // Edge Case 9: String with only one character
        System.out.println(solution.lengthOfLongestSubstring("a")); // 1
        // Edge Case 10: Long string with all unique then repeat at end
        System.out.println(solution.lengthOfLongestSubstring("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwx")); // 24
    }
}
