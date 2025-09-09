package tries.medium;

/**
 * LeetCode 720: Longest Word in Dictionary
 * https://leetcode.com/problems/longest-word-in-dictionary/
 *
 * Description:
 * Given an array of strings words representing an English Dictionary, return
 * the longest word in words that can be built one character at a time by other
 * words in words.
 *
 * Constraints:
 * - 1 <= words.length <= 1000
 * - 1 <= words[i].length <= 30
 * - words[i] consists of only lowercase English letters
 *
 * Follow-up:
 * - Can you solve it using a trie?
 * - Can you optimize for lexicographical ordering?
 * - Can you handle ties efficiently?
 */
public class LongestWordInDictionary {
    public String longestWord(String[] words) {
    TrieNode root = new TrieNode();

    for (String word : words) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) node.children[c - 'a'] = new TrieNode();
            node = node.children[c - 'a'];
        }
        node.isWord = true;
    }
    String[] result = { "" };

    dfs(root, new StringBuilder(), result);
        return result[0];
    }

    private void dfs(TrieNode node, StringBuilder sb, String[] result) {
        if (sb.length() > result[0].length())
            result[0] = sb.toString();
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children[c - 'a'];
            if (child != null && child.isWord) {
                sb.append(c);
                dfs(child, sb, result);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }
    
    public static void main(String[] args) {
        LongestWordInDictionary solution = new LongestWordInDictionary();

        System.out.println(solution.longestWord(new String[] { "w", "wo", "wor", "worl", "world" })); // "world"
        System.out.println(
                solution.longestWord(new String[] { "a", "banana", "app", "appl", "ap", "apply", "application" })); // "a"

        // Edge Case: No buildable words
        System.out.println(solution.longestWord(new String[] { "abc", "def", "ghi" })); // ""

        // Edge Case: Single character words
        System.out.println(solution.longestWord(new String[] { "a", "b", "c" })); // "a" (lexicographically smallest)

        // Edge Case: Tie in length
        System.out.println(solution.longestWord(new String[] { "a", "ab", "abc", "b", "bc", "bcd" })); // "abc"

        // Edge Case: All words same length
        System.out.println(solution.longestWord(new String[] { "cat", "bat", "rat" })); // ""

        // Edge Case: Complex buildable chain
        System.out.println(solution.longestWord(new String[] { "m", "mo", "moc", "moch", "mocha", "l", "la", "lat",
                "latt", "latte", "c", "ca", "cat" })); // "latte"
    }
}
