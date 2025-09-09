package tries.medium;

/**
 * LeetCode 1032: Stream of Characters
 * https://leetcode.com/problems/stream-of-characters/
 *
 * Description:
 * Design an algorithm that accepts a stream of characters and checks if a
 * suffix of these characters is a string of a given array of strings words.
 *
 * Constraints:
 * - 1 <= words.length <= 2000
 * - 1 <= words[i].length <= 2000
 * - words[i] consists of lowercase English letters only
 * - 1 <= query.length <= 40000
 * - query consists of lowercase English letters only
 *
 * Follow-up:
 * - Can you solve it using a reverse trie?
 * - Can you optimize for memory usage?
 * - Can you handle very long streams efficiently?
 */
public class StreamOfCharacters {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    private TrieNode root = new TrieNode();
    private StringBuilder sb = new StringBuilder();

    public StreamOfCharacters(String[] words) {
        for (String word : words) {
            TrieNode node = root;
            for (int i = word.length() - 1; i >= 0; i--) {
                char c = word.charAt(i);
                if (node.children[c - 'a'] == null)
                    node.children[c - 'a'] = new TrieNode();
                node = node.children[c - 'a'];
            }
            node.isWord = true;
        }
    }

    public boolean query(char letter) {
        sb.append(letter);
        TrieNode node = root;
        for (int i = sb.length() - 1; i >= 0 && node != null; i--) {
            char c = sb.charAt(i);
            node = node.children[c - 'a'];
            if (node != null && node.isWord)
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        StreamOfCharacters streamChecker = new StreamOfCharacters(new String[] { "cd", "f", "kl" });

        System.out.println(streamChecker.query('a')); // false
        System.out.println(streamChecker.query('b')); // false
        System.out.println(streamChecker.query('c')); // false
        System.out.println(streamChecker.query('d')); // true (stream: "abcd", suffix "cd" matches)
        System.out.println(streamChecker.query('e')); // false
        System.out.println(streamChecker.query('f')); // true (stream: "abcdef", suffix "f" matches)
        System.out.println(streamChecker.query('g')); // false
        System.out.println(streamChecker.query('h')); // false
        System.out.println(streamChecker.query('i')); // false
        System.out.println(streamChecker.query('j')); // false
        System.out.println(streamChecker.query('k')); // false
        System.out.println(streamChecker.query('l')); // true (stream: "abcdefghijkl", suffix "kl" matches)

        // Edge Case: Single character words
        StreamOfCharacters sc2 = new StreamOfCharacters(new String[] { "a", "b" });
        System.out.println(sc2.query('a')); // true
        System.out.println(sc2.query('b')); // true
        System.out.println(sc2.query('c')); // false

        // Edge Case: Overlapping words
        StreamOfCharacters sc3 = new StreamOfCharacters(new String[] { "abc", "bc", "c" });
        System.out.println(sc3.query('a')); // false
        System.out.println(sc3.query('b')); // false
        System.out.println(sc3.query('c')); // true (matches "c", "bc", "abc")
    }
}
