package tries.hard;

import java.util.*;

/**
 * LeetCode 336: Palindrome Pairs
 * https://leetcode.com/problems/palindrome-pairs/
 *
 * Description:
 * Given a list of unique words, return all the pairs of the distinct indices
 * (i, j) such that the concatenation of the two words words[i] + words[j] is a
 * palindrome.
 *
 * Constraints:
 * - 1 <= words.length <= 5000
 * - 0 <= words[i].length <= 300
 * - words[i] consists of lower-case English letters
 *
 * Follow-up:
 * - Can you solve it using a trie for efficient reverse lookup?
 * - Can you optimize for the case where many words are empty?
 * - Can you extend to find all palindromic substrings?
 */
public class PalindromePairs {
    public List<List<Integer>> palindromePairs(String[] words) {
        List<List<Integer>> result = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < words.length; i++)
            map.put(words[i], i);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            for (int j = 0; j <= word.length(); j++) {
                String left = word.substring(0, j), right = word.substring(j);
                if (isPalindrome(left)) {
                    String rev = new StringBuilder(right).reverse().toString();
                    if (map.containsKey(rev) && map.get(rev) != i)
                        result.add(Arrays.asList(map.get(rev), i));
                }
                if (j != word.length() && isPalindrome(right)) {
                    String rev = new StringBuilder(left).reverse().toString();
                    if (map.containsKey(rev) && map.get(rev) != i)
                        result.add(Arrays.asList(i, map.get(rev)));
                }
            }
        }
        return result;
    }

    private boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false;
        return true;
    }



    public static void main(String[] args) {
        PalindromePairs solution = new PalindromePairs();

        System.out.println(solution.palindromePairs(new String[] { "abcd", "dcba", "lls", "s", "sssll" }));
        // [[0,1],[1,0],[3,2],[2,4]]

        System.out.println(solution.palindromePairs(new String[] { "bat", "tab", "cat" }));
        // [[0,1],[1,0]]

        System.out.println(solution.palindromePairs(new String[] { "race", "car" }));
        // []

        // Edge Case: Empty string
        System.out.println(solution.palindromePairs(new String[] { "", "abc", "cba" }));
        // [[0,1],[0,2],[1,0],[2,0]]

        // Edge Case: Single character words
        System.out.println(solution.palindromePairs(new String[] { "a", "b", "c" }));
        // []

        // Edge Case: Palindromic words
        System.out.println(solution.palindromePairs(new String[] { "aba", "bab" }));
        // []

        // Edge Case: Large input
        String[] large = new String[1000];
        for (int i = 0; i < 1000; i++) {
            large[i] = "a" + i + "b";
        }
        System.out.println(solution.palindromePairs(large).size()); // Should handle efficiently
    }
}
