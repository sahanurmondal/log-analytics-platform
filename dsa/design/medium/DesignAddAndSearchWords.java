package design.medium;

import java.util.*;

/**
 * LeetCode 211: Design Add and Search Words Data Structure
 * https://leetcode.com/problems/design-add-and-search-words-data-structure/
 * 
 * Problem:
 * Design a data structure that supports adding new words and finding if a
 * string matches any previously added string.
 * Implement the WordDictionary class:
 * - WordDictionary() Initializes the object.
 * - void addWord(word) Adds word to the data structure, it can be matched
 * later.
 * - bool search(word) Returns true if there is any string in the data structure
 * that matches word or false otherwise.
 * word may contain dots '.' where dots can be matched with any letter.
 * 
 * Example:
 * Input
 * ["WordDictionary","addWord","addWord","addWord","search","search","search","search"]
 * [[],["bad"],["dad"],["mad"],["pad"],["bad"],[".ad"],["b.."]]
 * Output
 * [null,null,null,null,false,true,true,true]
 * 
 * Explanation:
 * WordDictionary wordDictionary = new WordDictionary();
 * wordDictionary.addWord("bad");
 * wordDictionary.addWord("dad");
 * wordDictionary.addWord("mad");
 * wordDictionary.search("pad"); // return False
 * wordDictionary.search("bad"); // return True
 * wordDictionary.search(".ad"); // return True
 * wordDictionary.search("b.."); // return True
 * 
 * Constraints:
 * 1 <= word.length <= 25
 * word in addWord consists of lowercase English letters.
 * word in search consist of '.' or lowercase English letters.
 * There will be at most 2 dots in word for search queries.
 * At most 10^4 calls will be made to addWord and search.
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Frequency: Very High
 */
public class DesignAddAndSearchWords {

    // Trie Node class
    class TrieNode {
        TrieNode[] children;
        boolean isWord;

        public TrieNode() {
            children = new TrieNode[26]; // for 'a' to 'z'
            isWord = false;
        }
    }

    private TrieNode root;

    /** Initialize your data structure here. */
    public DesignAddAndSearchWords() {
        root = new TrieNode();
    }

    /** Adds a word into the data structure. */
    public void addWord(String word) {
        TrieNode node = root;

        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }

        node.isWord = true;
    }

    /**
     * Returns if the word is in the data structure. A word could contain the dot
     * character '.' to represent any one letter.
     */
    public boolean search(String word) {
        return searchHelper(word, 0, root);
    }

    private boolean searchHelper(String word, int index, TrieNode node) {
        // Base case: reached end of word
        if (index == word.length()) {
            return node.isWord;
        }

        char c = word.charAt(index);

        if (c == '.') {
            // Try all possible children
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null && searchHelper(word, index + 1, node.children[i])) {
                    return true;
                }
            }
            return false;
        } else {
            // Regular character
            int charIndex = c - 'a';
            if (node.children[charIndex] == null) {
                return false;
            }
            return searchHelper(word, index + 1, node.children[charIndex]);
        }
    }

    /**
     * Alternative implementation using iterative approach for addWord
     */
    public void addWordIterative(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = c - 'a';

            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }

        current.isWord = true;
    }

    /**
     * Follow-up: Get all words with a given prefix
     */
    public List<String> getWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = root;

        // Navigate to prefix
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                return result; // No words with this prefix
            }
            node = node.children[index];
        }

        // DFS to collect all words
        collectWords(node, prefix, result);
        return result;
    }

    private void collectWords(TrieNode node, String prefix, List<String> result) {
        if (node.isWord) {
            result.add(prefix);
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                collectWords(node.children[i], prefix + (char) ('a' + i), result);
            }
        }
    }

    /**
     * Follow-up: Delete a word
     */
    public boolean deleteWord(String word) {
        return deleteHelper(root, word, 0);
    }

    private boolean deleteHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            if (!node.isWord) {
                return false; // Word doesn't exist
            }
            node.isWord = false;

            // Check if this node has any children
            for (TrieNode child : node.children) {
                if (child != null) {
                    return false; // Don't delete this node
                }
            }
            return true; // Can delete this node
        }

        char c = word.charAt(index);
        int charIndex = c - 'a';
        TrieNode child = node.children[charIndex];

        if (child == null) {
            return false; // Word doesn't exist
        }

        boolean shouldDelete = deleteHelper(child, word, index + 1);

        if (shouldDelete) {
            node.children[charIndex] = null;

            // Check if current node should be deleted
            if (!node.isWord) {
                for (TrieNode childNode : node.children) {
                    if (childNode != null) {
                        return false;
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        DesignAddAndSearchWords wordDictionary = new DesignAddAndSearchWords();

        // Test the example
        wordDictionary.addWord("bad");
        wordDictionary.addWord("dad");
        wordDictionary.addWord("mad");

        System.out.println("search('pad'): " + wordDictionary.search("pad")); // false
        System.out.println("search('bad'): " + wordDictionary.search("bad")); // true
        System.out.println("search('.ad'): " + wordDictionary.search(".ad")); // true
        System.out.println("search('b..'): " + wordDictionary.search("b..")); // true

        // Additional tests
        wordDictionary.addWord("a");
        wordDictionary.addWord("a");
        System.out.println("search('.'): " + wordDictionary.search(".")); // true
        System.out.println("search('a'): " + wordDictionary.search("a")); // true
        System.out.println("search('aa'): " + wordDictionary.search("aa")); // false
        System.out.println("search('.a'): " + wordDictionary.search(".a")); // false
        System.out.println("search('a.'): " + wordDictionary.search("a.")); // false

        // Test prefix search
        System.out.println("\nWords with prefix 'b': " + wordDictionary.getWordsWithPrefix("b")); // [bad]
        System.out.println("Words with prefix 'd': " + wordDictionary.getWordsWithPrefix("d")); // [dad]

        System.out.println("\nAll test cases completed successfully!");
    }
}
