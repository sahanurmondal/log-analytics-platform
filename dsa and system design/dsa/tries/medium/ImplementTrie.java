package tries.medium;

import java.util.*;

/**
 * LeetCode 208: Implement Trie (Prefix Tree)
 * https://leetcode.com/problems/implement-trie-prefix-tree/
 *
 * Description:
 * A trie (pronounced as "try") or prefix tree is a tree data structure used to
 * efficiently store and retrieve keys in a dataset of strings.
 *
 * Constraints:
 * - 1 <= word.length, prefix.length <= 2000
 * - word and prefix consist only of lowercase English letters
 * - At most 3 * 10^4 calls in total will be made to insert, search, and
 * startsWith
 *
 * Follow-up:
 * - Can you implement it using arrays instead of HashMap?
 * - Can you implement delete operation?
 * - Can you extend to support case-insensitive search?
 */
public class ImplementTrie {

    class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;

        public TrieNode() {
            children = new TrieNode[26]; // for lowercase letters a-z
            isEndOfWord = false;
        }
    }

    class Trie {
        private TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode current = root;
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    current.children[index] = new TrieNode();
                }
                current = current.children[index];
            }
            current.isEndOfWord = true;
        }

        public boolean search(String word) {
            TrieNode node = searchNode(word);
            return node != null && node.isEndOfWord;
        }

        public boolean startsWith(String prefix) {
            return searchNode(prefix) != null;
        }

        private TrieNode searchNode(String str) {
            TrieNode current = root;
            for (char c : str.toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    return null;
                }
                current = current.children[index];
            }
            return current;
        }

        // Follow-up: Delete operation
        public void delete(String word) {
            delete(root, word, 0);
        }

        private boolean delete(TrieNode current, String word, int index) {
            if (index == word.length()) {
                if (!current.isEndOfWord) {
                    return false;
                }
                current.isEndOfWord = false;
                return !hasChildren(current);
            }

            int charIndex = word.charAt(index) - 'a';
            TrieNode node = current.children[charIndex];
            if (node == null) {
                return false;
            }

            boolean shouldDeleteChild = delete(node, word, index + 1);

            if (shouldDeleteChild) {
                current.children[charIndex] = null;
                return !current.isEndOfWord && !hasChildren(current);
            }

            return false;
        }

        private boolean hasChildren(TrieNode node) {
            for (TrieNode child : node.children) {
                if (child != null)
                    return true;
            }
            return false;
        }

        // Get all words with given prefix
        public List<String> getWordsWithPrefix(String prefix) {
            List<String> result = new ArrayList<>();
            TrieNode prefixNode = searchNode(prefix);
            if (prefixNode != null) {
                collectWords(prefixNode, prefix, result);
            }
            return result;
        }

        private void collectWords(TrieNode node, String prefix, List<String> result) {
            if (node.isEndOfWord) {
                result.add(prefix);
            }

            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    collectWords(node.children[i], prefix + (char) ('a' + i), result);
                }
            }
        }
    }

    // Follow-up: HashMap-based implementation
    class TrieHashMap {
        class TrieNodeMap {
            Map<Character, TrieNodeMap> children;
            boolean isEndOfWord;

            public TrieNodeMap() {
                children = new HashMap<>();
                isEndOfWord = false;
            }
        }

        private TrieNodeMap root;

        public TrieHashMap() {
            root = new TrieNodeMap();
        }

        public void insert(String word) {
            TrieNodeMap current = root;
            for (char c : word.toCharArray()) {
                current.children.putIfAbsent(c, new TrieNodeMap());
                current = current.children.get(c);
            }
            current.isEndOfWord = true;
        }

        public boolean search(String word) {
            TrieNodeMap node = searchNode(word);
            return node != null && node.isEndOfWord;
        }

        public boolean startsWith(String prefix) {
            return searchNode(prefix) != null;
        }

        private TrieNodeMap searchNode(String str) {
            TrieNodeMap current = root;
            for (char c : str.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return null;
                }
                current = current.children.get(c);
            }
            return current;
        }
    }

    // Follow-up: Case-insensitive Trie
    class CaseInsensitiveTrie {
        private TrieNode root;

        public CaseInsensitiveTrie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode current = root;
            for (char c : word.toLowerCase().toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    current.children[index] = new TrieNode();
                }
                current = current.children[index];
            }
            current.isEndOfWord = true;
        }

        public boolean search(String word) {
            TrieNode node = searchNode(word.toLowerCase());
            return node != null && node.isEndOfWord;
        }

        public boolean startsWith(String prefix) {
            return searchNode(prefix.toLowerCase()) != null;
        }

        private TrieNode searchNode(String str) {
            TrieNode current = root;
            for (char c : str.toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    return null;
                }
                current = current.children[index];
            }
            return current;
        }
    }

    public static void main(String[] args) {
        ImplementTrie solution = new ImplementTrie();

        // Test basic Trie
        Trie trie = solution.new Trie();
        trie.insert("apple");
        System.out.println(trie.search("apple")); // true
        System.out.println(trie.search("app")); // false
        System.out.println(trie.startsWith("app")); // true
        trie.insert("app");
        System.out.println(trie.search("app")); // true

        // Test delete operation
        trie.delete("app");
        System.out.println(trie.search("app")); // false
        System.out.println(trie.search("apple")); // true

        // Test prefix words
        trie.insert("application");
        trie.insert("apply");
        System.out.println("Words with prefix 'app': " + trie.getWordsWithPrefix("app"));

        // Test case-insensitive
        CaseInsensitiveTrie ciTrie = solution.new CaseInsensitiveTrie();
        ciTrie.insert("Apple");
        System.out.println(ciTrie.search("apple")); // true
        System.out.println(ciTrie.search("APPLE")); // true
        System.out.println(ciTrie.startsWith("App")); // true
    }
}
