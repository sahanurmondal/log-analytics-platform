package searching.medium;

import java.util.*;

/**
 * LeetCode 1268: Search Suggestions System
 * https://leetcode.com/problems/search-suggestions-system/
 *
 * Description:
 * Given an array of strings products and a string searchWord. We want to design
 * a system that suggests at most three product names from products after each
 * character of searchWord is typed.
 *
 * Constraints:
 * - 1 <= products.length <= 1000
 * - 1 <= products[i].length <= 3000
 * - 1 <= searchWord.length <= 1000
 * - All strings are composed of lowercase English letters only
 *
 * Follow-up:
 * - Can you solve it using binary search?
 * - Can you solve it using Trie?
 * - Can you optimize for multiple search queries?
 */
public class SearchSuggestionsSystem {
    public List<List<String>> suggestedProducts(String[] products, String searchWord) {
        Arrays.sort(products);
        List<List<String>> res = new ArrayList<>();
        String prefix = "";
        for (char c : searchWord.toCharArray()) {
            prefix += c;
            List<String> curr = new ArrayList<>();
            int idx = Arrays.binarySearch(products, prefix);
            if (idx < 0) idx = -idx - 1;
            for (int i = idx; i < products.length && curr.size() < 3; i++) {
                if (products[i].startsWith(prefix)) curr.add(products[i]);
                else break;
            }
            res.add(curr);
        }
        return res;
    }

    // Follow-up: Trie solution
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        List<String> suggestions = new ArrayList<>();
    }
    public List<List<String>> suggestedProductsTrie(String[] products, String searchWord) {
        TrieNode root = new TrieNode();
        Arrays.sort(products);
        for (String prod : products) {
            TrieNode node = root;
            for (char c : prod.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null) node.children[idx] = new TrieNode();
                node = node.children[idx];
                if (node.suggestions.size() < 3) node.suggestions.add(prod);
            }
        }
        List<List<String>> res = new ArrayList<>();
        TrieNode node = root;
        for (char c : searchWord.toCharArray()) {
            if (node != null) node = node.children[c - 'a'];
            res.add(node == null ? new ArrayList<>() : node.suggestions);
        }
        return res;
    }

    public static void main(String[] args) {
        SearchSuggestionsSystem solution = new SearchSuggestionsSystem();
        System.out.println(solution.suggestedProducts(new String[] { "mobile", "mouse", "moneypot", "monitor", "mousepad" }, "mouse"));
        // [["mobile","moneypot","monitor"],["mobile","moneypot","monitor"],["mouse","mousepad"],["mouse","mousepad"],["mouse","mousepad"]]

        System.out.println(solution.suggestedProducts(new String[] { "havana" }, "havana"));
        // [["havana"],["havana"],["havana"],["havana"],["havana"],["havana"]]

        // Edge Case: No matches
        System.out.println(solution.suggestedProducts(new String[] { "mobile", "mouse" }, "xyz")); // [[],[],[]]

        // Edge Case: Single character search
        System.out.println(solution.suggestedProducts(new String[] { "apple", "banana" }, "a")); // [["apple"]]
        // Trie follow-up
        System.out.println(solution.suggestedProductsTrie(new String[] { "mobile", "mouse", "moneypot", "monitor", "mousepad" }, "mouse"));
    }
}
