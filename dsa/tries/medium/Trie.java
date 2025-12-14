package tries.medium;

import java.util.*;

/**
 * LeetCode 208: Implement Trie (Prefix Tree)
 * https://leetcode.com/problems/implement-trie-prefix-tree/
 *
 * A Trie (pronounced "try") is a tree data structure used to efficiently
 * store and retrieve keys in a dataset of strings.
 *
 * Features:
 * - insert(word): Insert word into trie in O(m) time, where m = word length
 * - search(word): Search for exact word in O(m) time
 * - startsWith(prefix): Check if prefix exists in O(m) time
 *
 * Example:
 * Trie trie = new Trie();
 * trie.insert("apple");
 * trie.search("apple");        // returns true
 * trie.search("app");          // returns false
 * trie.startsWith("app");      // returns true
 * trie.insert("app");
 * trie.search("app");          // returns true
 */
public class Trie {

    class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;

        public TrieNode() {
            children = new TrieNode[26]; // for lowercase letters a-z
            isEndOfWord = false;
        }
    }

    private TrieNode root;

    /**
     * Initialize the trie with empty root
     */
    public Trie() {
        root = new TrieNode();
    }

    /**
     * Insert word into trie
     * Time: O(m), where m = word.length()
     * Space: O(m) for new nodes
     */
    public void insert(String word) {
        TrieNode current = root;

        for (char c : word.toCharArray()) {
            int index = c - 'a';

            // Create new node if it doesn't exist
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }

            current = current.children[index];
        }

        // Mark the end of word
        current.isEndOfWord = true;
    }

    /**
     * Search for exact word in trie
     * Time: O(m), where m = word.length()
     * Space: O(1)
     */
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Check if prefix exists in trie
     * Time: O(m), where m = prefix.length()
     * Space: O(1)
     */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    /**
     * Helper method to find node for given word/prefix
     * Returns the node if path exists, null otherwise
     */
    private TrieNode findNode(String word) {
        TrieNode current = root;

        for (char c : word.toCharArray()) {
            int index = c - 'a';

            // If path doesn't exist, return null
            if (current.children[index] == null) {
                return null;
            }

            current = current.children[index];
        }

        return current;
    }

    /**
     * Delete a word from trie
     * Time: O(m), Space: O(1)
     */
    public boolean delete(String word) {
        return deleteHelper(root, word, 0);
    }

    private boolean deleteHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            // Word not found
            if (!node.isEndOfWord) {
                return false;
            }

            // Mark as not end of word
            node.isEndOfWord = false;

            // Return true if node has no children (can be deleted)
            return hasNoChildren(node);
        }

        int charIndex = word.charAt(index) - 'a';
        TrieNode child = node.children[charIndex];

        if (child == null) {
            return false; // Word not found
        }

        boolean shouldDeleteChild = deleteHelper(child, word, index + 1);

        if (shouldDeleteChild) {
            node.children[charIndex] = null;
            // Return true if current node should be deleted
            return !node.isEndOfWord && hasNoChildren(node);
        }

        return false;
    }

    private boolean hasNoChildren(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get all words in trie
     * Time: O(n * m), where n = number of words, m = avg word length
     */
    public List<String> getAllWords() {
        List<String> result = new ArrayList<>();
        dfs(root, new StringBuilder(), result);
        return result;
    }

    private void dfs(TrieNode node, StringBuilder path, List<String> result) {
        if (node.isEndOfWord) {
            result.add(path.toString());
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                path.append((char) ('a' + i));
                dfs(node.children[i], path, result);
                path.deleteCharAt(path.length() - 1);
            }
        }
    }

    /**
     * Get all words with given prefix
     * Time: O(n * m), where n = words with prefix, m = avg word length
     */
    public List<String> getWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = findNode(prefix);

        if (node != null) {
            dfs(node, new StringBuilder(prefix), result);
        }

        return result;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();

        // Test basic operations
        trie.insert("apple");
        System.out.println("search('apple'): " + trie.search("apple"));      // true
        System.out.println("search('app'): " + trie.search("app"));          // false
        System.out.println("startsWith('app'): " + trie.startsWith("app")); // true

        trie.insert("app");
        System.out.println("search('app') after insert: " + trie.search("app")); // true

        // Test with multiple words
        trie.insert("apricot");
        trie.insert("apply");
        trie.insert("banana");

        System.out.println("\nAll words: " + trie.getAllWords());
        System.out.println("Words with prefix 'app': " + trie.getWordsWithPrefix("app"));
        System.out.println("Words with prefix 'ban': " + trie.getWordsWithPrefix("ban"));

        // Test delete
        System.out.println("\nBefore delete 'apple': " + trie.search("apple")); // true
        trie.delete("apple");
        System.out.println("After delete 'apple': " + trie.search("apple")); // false
        System.out.println("'app' still exists: " + trie.search("app"));     // true
    }
}

