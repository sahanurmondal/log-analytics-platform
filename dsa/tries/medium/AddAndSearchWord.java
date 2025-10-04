package tries.medium;

/**
 * LeetCode 211: Design Add and Search Words Data Structure
 * https://leetcode.com/problems/design-add-and-search-words-data-structure/
 *
 * Description:
 * Design a data structure that supports adding new words and finding if a
 * string matches any previously added string.
 * The search function can search for a literal word or a regular expression
 * string containing '.' where '.' can represent any letter.
 *
 * Constraints:
 * - 1 <= word.length <= 25
 * - word in addWord consists of lowercase English letters
 * - word in search consist of '.' or lowercase English letters
 * - There will be at most 3 calls to addWord for every one call to search
 *
 * Follow-up:
 * - Can you optimize the search with wildcards?
 * - Can you extend to support other regex patterns?
 * - Can you handle case-insensitive matching?
 */
public class AddAndSearchWord {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    private TrieNode root = new TrieNode();

    public AddAndSearchWord() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null)
                node.children[c - 'a'] = new TrieNode();
            node = node.children[c - 'a'];
        }
        node.isWord = true;
    }

    public boolean search(String word) {
        return searchHelper(word, 0, root);
    }

    private boolean searchHelper(String word, int idx, TrieNode node) {
        if (idx == word.length()) return node.isWord;
        char c = word.charAt(idx);
        if (c == '.') {
            for (TrieNode child : node.children) {
                if (child != null && searchHelper(word, idx + 1, child)) return true;
            }
            return false;
        } else {
            if (node.children[c - 'a'] == null) return false;
            return searchHelper(word, idx + 1, node.children[c - 'a']);
        }
    }

    public static void main(String[] args) {
        AddAndSearchWord wordDictionary = new AddAndSearchWord();

        wordDictionary.addWord("bad");
        wordDictionary.addWord("dad");
        wordDictionary.addWord("mad");

        System.out.println(wordDictionary.search("pad")); // false
        System.out.println(wordDictionary.search("bad")); // true
        System.out.println(wordDictionary.search(".ad")); // true
        System.out.println(wordDictionary.search("b..")); // true

        // Edge Case: All wildcards
        wordDictionary.addWord("abc");
        System.out.println(wordDictionary.search("...")); // true

        // Edge Case: Single character
        wordDictionary.addWord("a");
        System.out.println(wordDictionary.search(".")); // true

        // Edge Case: Empty pattern
        System.out.println(wordDictionary.search("")); // Should handle gracefully

        // Edge Case: Long word with wildcards
        wordDictionary.addWord("abcdefg");
        System.out.println(wordDictionary.search("a.c.e.g")); // true
        System.out.println(wordDictionary.search("a.c.e.h")); // false
    }
}
