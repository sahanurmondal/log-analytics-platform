package design.medium;

/**
 * LeetCode 211: Design Add and Search Words Data Structure
 * https://leetcode.com/problems/design-add-and-search-words-data-structure/
 *
 * Description: Design a data structure that supports adding new words and
 * finding if a string matches any previously added string.
 * 
 * Constraints:
 * - 1 <= word.length <= 25
 * - word in addWord consists of lowercase English letters
 * - word in search consist of '.' or lowercase English letters
 * - There will be at most 2 dots in word for search queries
 * - At most 10^4 calls will be made to addWord and search
 *
 * Follow-up:
 * - Can you solve it using Trie?
 * 
 * Time Complexity: O(m) for addWord, O(m * n) for search in worst case
 * Space Complexity: O(total number of characters in all words)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class WordDictionary {

    class TrieNode {
        TrieNode[] children;
        boolean isWord;

        public TrieNode() {
            children = new TrieNode[26];
            isWord = false;
        }
    }

    private TrieNode root;

    public WordDictionary() {
        root = new TrieNode();
    }

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

    public boolean search(String word) {
        return searchHelper(word, 0, root);
    }

    private boolean searchHelper(String word, int index, TrieNode node) {
        if (index == word.length()) {
            return node.isWord;
        }

        char c = word.charAt(index);
        if (c == '.') {
            // Try all possible characters
            for (TrieNode child : node.children) {
                if (child != null && searchHelper(word, index + 1, child)) {
                    return true;
                }
            }
            return false;
        } else {
            int childIndex = c - 'a';
            TrieNode child = node.children[childIndex];
            return child != null && searchHelper(word, index + 1, child);
        }
    }

    public static void main(String[] args) {
        WordDictionary wordDictionary = new WordDictionary();
        wordDictionary.addWord("bad");
        wordDictionary.addWord("dad");
        wordDictionary.addWord("mad");
        System.out.println(wordDictionary.search("pad")); // Expected: false
        System.out.println(wordDictionary.search("bad")); // Expected: true
        System.out.println(wordDictionary.search(".ad")); // Expected: true
        System.out.println(wordDictionary.search("b..")); // Expected: true
    }
}
