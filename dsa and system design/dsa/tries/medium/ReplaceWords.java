package tries.medium;

import java.util.List;

/**
 * LeetCode 648: Replace Words
 * https://leetcode.com/problems/replace-words/
 *
 * Description:
 * In English, we have a concept called root, which can be followed by some
 * other word to form another longer word - let's call this word successor.
 * Given a dictionary consisting of many roots and a sentence consisting of
 * words separated by spaces, replace all the successors in the sentence with
 * the root forming it.
 *
 * Constraints:
 * - 1 <= dictionary.length <= 1000
 * - 1 <= dictionary[i].length <= 100
 * - dictionary[i] consists of only lower-case letters
 * - 1 <= sentence.length <= 10^6
 * - sentence consists of only lower-case letters and spaces
 *
 * Follow-up:
 * - Can you solve it using a trie for efficient prefix matching?
 * - Can you handle multiple valid roots for the same word?
 * - Can you optimize for very large sentences?
 */
public class ReplaceWords {
    public String replaceWords(List<String> dictionary, String sentence) {
        TrieNode root = new TrieNode();
        for (String word : dictionary) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                if (node.children[c - 'a'] == null)
                    node.children[c - 'a'] = new TrieNode();
                node = node.children[c - 'a'];
            }
            node.isWord = true;
        }
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            TrieNode node = root;
            StringBuilder sb = new StringBuilder();
            for (char c : words[i].toCharArray()) {
                if (node.children[c - 'a'] == null || node.isWord)
                    break;
                sb.append(c);
                node = node.children[c - 'a'];
            }
            words[i] = node.isWord ? sb.toString() : words[i];
        }
        return String.join(" ", words);
    }

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    public static void main(String[] args) {
        ReplaceWords solution = new ReplaceWords();

        List<String> dict1 = java.util.Arrays.asList("cat", "bat", "rat");
        System.out.println(solution.replaceWords(dict1, "the cattle was rattled by the battery"));
        // "the cat was rat by the bat"

        List<String> dict2 = java.util.Arrays.asList("a", "aa", "aaa", "aaaa");
        System.out.println(solution.replaceWords(dict2, "a aa a aaaa aaa aaa aaa aaaaaa bbb baba ababa"));
        // "a a a a a a a a bbb baba a"

        // Edge Case: No replacements
        List<String> dict3 = java.util.Arrays.asList("xyz");
        System.out.println(solution.replaceWords(dict3, "hello world")); // "hello world"

        // Edge Case: All words replaced
        List<String> dict4 = java.util.Arrays.asList("a", "b");
        System.out.println(solution.replaceWords(dict4, "apple banana")); // "a b"

        // Edge Case: Empty sentence
        List<String> dict5 = java.util.Arrays.asList("cat");
        System.out.println(solution.replaceWords(dict5, "")); // ""

        // Edge Case: Single word sentence
        List<String> dict6 = java.util.Arrays.asList("cat");
        System.out.println(solution.replaceWords(dict6, "category")); // "cat"
    }
}
