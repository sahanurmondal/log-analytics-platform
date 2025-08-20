package arrays.medium;

/**
 * LeetCode 208: Implement Trie (Prefix Tree)
 * https://leetcode.com/problems/implement-trie-prefix-tree/
 *
 * Description:
 * A trie (pronounced as "try") or prefix tree is a tree data structure used to
 * efficiently store and retrieve
 * keys in a dataset of strings. There are various applications of this data
 * structure, such as autocomplete and spellchecker.
 * Implement the Trie class with insert, search, and startsWith methods.
 *
 * Constraints:
 * - 1 <= word.length, prefix.length <= 2000
 * - word and prefix consist only of lowercase English letters
 * - At most 3 * 10^4 calls in total will be made to insert, search, and
 * startsWith
 *
 * Follow-up:
 * - Can you implement it more space efficiently?
 * 
 * Time Complexity: O(m) for all operations where m = word/prefix length
 * Space Complexity: O(ALPHABET_SIZE * N * M) where N = number of words, M =
 * average length
 * 
 * Algorithm:
 * 1. Use TrieNode with children array and isWord flag
 * 2. Insert: traverse/create nodes for each character
 * 3. Search/StartsWith: traverse nodes checking existence
 */
public class ImplementTrie {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;

        public TrieNode() {
            children = new TrieNode[26];
            isWord = false;
        }
    }

    class Trie {
        private TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
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

        public boolean search(String word) {
            TrieNode node = findNode(word);
            return node != null && node.isWord;
        }

        public boolean startsWith(String prefix) {
            return findNode(prefix) != null;
        }

        private TrieNode findNode(String str) {
            TrieNode node = root;
            for (char c : str.toCharArray()) {
                int index = c - 'a';
                if (node.children[index] == null) {
                    return null;
                }
                node = node.children[index];
            }
            return node;
        }
    }

    public static void main(String[] args) {
        ImplementTrie solution = new ImplementTrie();
        ImplementTrie.Trie trie = solution.new Trie();

        // Test Case 1: Normal case - insert and search
        trie.insert("apple");
        System.out.println(trie.search("apple")); // Expected: true

        // Test Case 2: Edge case - search non-existent
        System.out.println(trie.search("app")); // Expected: false

        // Test Case 3: Corner case - prefix exists
        System.out.println(trie.startsWith("app")); // Expected: true

        // Test Case 4: Insert more words
        trie.insert("app");
        System.out.println(trie.search("app")); // Expected: true

        // Test Case 5: Minimum input - single char
        trie.insert("a");
        System.out.println(trie.search("a")); // Expected: true

        // Test Case 6: Special case - empty string handling
        System.out.println(trie.startsWith("")); // Expected: true

        // Test Case 7: Boundary case - long word
        trie.insert("application");
        System.out.println(trie.search("application")); // Expected: true

        // Test Case 8: Overlapping prefixes
        trie.insert("cat");
        trie.insert("car");
        System.out.println(trie.startsWith("ca")); // Expected: true

        // Test Case 9: Similar words
        System.out.println(trie.search("cat")); // Expected: true
        System.out.println(trie.search("car")); // Expected: true

        // Test Case 10: Non-existent with common prefix
        System.out.println(trie.search("card")); // Expected: false
    }
}
