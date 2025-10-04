package design.medium;

import java.util.*;

/**
 * LeetCode 745: Prefix and Suffix Search
 * https://leetcode.com/problems/prefix-and-suffix-search/
 *
 * Description: Design a special dictionary that searches words by a prefix and
 * a suffix.
 * 
 * Constraints:
 * - 1 <= words.length <= 10^4
 * - 1 <= words[i].length <= 7
 * - 1 <= prefix.length, suffix.length <= 7
 * - words[i], prefix and suffix consist of lowercase English letters only
 * - At most 10^4 calls will be made to the function f
 *
 * Follow-up:
 * - Can you optimize using Trie?
 * 
 * Time Complexity: O(n * m^2) for constructor, O(p + s) for f
 * Space Complexity: O(n * m^2)
 * 
 * Company Tags: Google, Amazon
 */
public class DesignWordFilter {

    class TrieNode {
        TrieNode[] children;
        int weight;

        TrieNode() {
            children = new TrieNode[27]; // 26 letters + 1 for separator
            weight = -1;
        }
    }

    private TrieNode root;

    public DesignWordFilter(String[] words) {
        root = new TrieNode();

        for (int weight = 0; weight < words.length; weight++) {
            String word = words[weight];
            for (int i = 0; i <= word.length(); i++) {
                insert(word.substring(i) + "{" + word, weight);
            }
        }
    }

    private void insert(String word, int weight) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int index = c == '{' ? 26 : c - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
            node.weight = weight;
        }
    }

    public int f(String prefix, String suffix) {
        TrieNode node = root;
        String query = suffix + "{" + prefix;

        for (char c : query.toCharArray()) {
            int index = c == '{' ? 26 : c - 'a';
            if (node.children[index] == null) {
                return -1;
            }
            node = node.children[index];
        }

        return node.weight;
    }

    // Alternative approach - Brute force with optimization
    static class DesignWordFilterBruteForce {
        private String[] words;

        public DesignWordFilterBruteForce(String[] words) {
            this.words = words;
        }

        public int f(String prefix, String suffix) {
            for (int i = words.length - 1; i >= 0; i--) {
                if (words[i].startsWith(prefix) && words[i].endsWith(suffix)) {
                    return i;
                }
            }
            return -1;
        }
    }

    public static void main(String[] args) {
        DesignWordFilter wf = new DesignWordFilter(new String[] { "apple" });
        System.out.println(wf.f("a", "e")); // Expected: 0
        System.out.println(wf.f("b", "e")); // Expected: -1

        DesignWordFilter wf2 = new DesignWordFilter(new String[] { "cabaaa", "ccbcab", "cabccc" });
        System.out.println(wf2.f("c", "a")); // Expected: 0
        System.out.println(wf2.f("c", "b")); // Expected: 1
        System.out.println(wf2.f("ca", "aa")); // Expected: 0
    }
}
