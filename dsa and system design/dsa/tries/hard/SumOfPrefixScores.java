package tries.hard;

/**
 * LeetCode 2416: Sum of Prefix Scores of Strings
 * https://leetcode.com/problems/sum-of-prefix-scores-of-strings/
 *
 * Description:
 * You are given an array words of size n consisting of non-empty strings.
 * We define the score of a string term as the number of strings words[i] such
 * that term is a prefix of words[i].
 *
 * Constraints:
 * - 1 <= words.length <= 1000
 * - 1 <= words[i].length <= 1000
 * - words[i] consists of lowercase English letters
 *
 * Follow-up:
 * - Can you solve it using a trie with prefix counts?
 * - Can you optimize for memory usage?
 * - Can you handle very large input efficiently?
 */
public class SumOfPrefixScores {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        int count = 0;
    }

    public int[] sumPrefixScores(String[] words) {
        TrieNode root = new TrieNode();
        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                if (node.children[c - 'a'] == null) {
                    node.children[c - 'a'] = new TrieNode();
                }
                node = node.children[c - 'a'];
                node.count++;
            }
        }

        int[] ans = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            TrieNode node = root;
            int score = 0;
            for (char c : words[i].toCharArray()) {
                node = node.children[c - 'a'];
                score += node.count;
            }
            ans[i] = score;
        }
        return ans;
    }

    public static void main(String[] args) {
        SumOfPrefixScores solution = new SumOfPrefixScores();

        System.out
                .println(java.util.Arrays.toString(solution.sumPrefixScores(new String[] { "abc", "ab", "bc", "b" })));
        // [5,4,3,2]

        System.out.println(java.util.Arrays.toString(solution.sumPrefixScores(new String[] { "abcd" })));
        // [4]

        // Edge Case: All words same
        System.out.println(java.util.Arrays.toString(solution.sumPrefixScores(new String[] { "a", "a", "a" })));
        // [3,3,3]

        // Edge Case: No common prefixes
        System.out.println(java.util.Arrays.toString(solution.sumPrefixScores(new String[] { "abc", "def", "ghi" })));
        // [3,3,3]

        // Edge Case: Nested prefixes
        System.out.println(
                java.util.Arrays.toString(solution.sumPrefixScores(new String[] { "a", "ab", "abc", "abcd" })));
        // [10,9,7,4]

        // Edge Case: Single character words
        System.out.println(java.util.Arrays.toString(solution.sumPrefixScores(new String[] { "a", "b", "c" })));
        // [1,1,1]

        // Edge Case: Large input
        String[] large = new String[1000];
        for (int i = 0; i < 1000; i++) {
            large[i] = "prefix" + i;
        }
        int[] result = solution.sumPrefixScores(large);
        System.out.println(result[0]); // Should handle efficiently
    }
}
