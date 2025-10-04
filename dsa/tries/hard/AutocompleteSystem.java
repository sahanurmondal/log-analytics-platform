package tries.hard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * LeetCode 642: Design Search Autocomplete System
 * https://leetcode.com/problems/design-search-autocomplete-system/
 *
 * Description:
 * Design a search autocomplete system for a search engine. Users may input a
 * sentence (at least one word and end with a special character '#').
 *
 * Constraints:
 * - n == sentences.length
 * - n == times.length
 * - 1 <= n <= 100
 * - 1 <= sentences[i].length <= 100
 * - 1 <= times[i] <= 50
 * - c is a lowercase English letter, a hash '#', or space ' '
 *
 * Follow-up:
 * - Can you optimize for real-time performance?
 * - Can you handle very large dictionaries?
 * - Can you support fuzzy matching?
 */
public class AutocompleteSystem {
    static class TrieNode {
        TrieNode[] children = new TrieNode[128];
        Map<String, Integer> counts = new java.util.HashMap<>();
    }

    private TrieNode root = new TrieNode();
    private StringBuilder sb = new StringBuilder();

    public AutocompleteSystem(String[] sentences, int[] times) {
        for (int i = 0; i < sentences.length; i++) {
            add(sentences[i], times[i]);
        }
    }

    public List<String> input(char c) {
        if (c == '#') {
            add(sb.toString(), 1);
            sb = new StringBuilder();
            return new ArrayList<>();
        }
        sb.append(c);
        TrieNode node = root;
        for (char ch : sb.toString().toCharArray()) {
            if (node.children[ch] == null) {
                return new ArrayList<>();
            }
            node = node.children[ch];
        }
        TrieNode finalNode = node;
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> {
            int cmp = finalNode.counts.get(b) - finalNode.counts.get(a);
            return cmp != 0 ? cmp : a.compareTo(b);
        });
        pq.addAll(finalNode.counts.keySet());
        List<String> res = new ArrayList<>();
        for (int i = 0; i < 3 && !pq.isEmpty(); i++) {
            res.add(pq.poll());
        }
        return res;
    }

    private void add(String s, int count) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            if (node.children[c] == null)
                node.children[c] = new TrieNode();
            node = node.children[c];
            node.counts.put(s, node.counts.getOrDefault(s, 0) + count);
        }
    }

    public static void main(String[] args) {
        AutocompleteSystem autocomplete = new AutocompleteSystem(
                new String[] { "i love you", "island", "iroman", "i love leetcode" },
                new int[] { 5, 3, 2, 2 });

        System.out.println(autocomplete.input('i')); // ["i love you", "island", "i love leetcode"]
        System.out.println(autocomplete.input(' ')); // ["i love you", "i love leetcode"]
        System.out.println(autocomplete.input('a')); // []
        System.out.println(autocomplete.input('#')); // [] (stores "i a" with frequency 1)

        // Now "i a" should appear in suggestions
        System.out.println(autocomplete.input('i')); // ["i love you", "island", "i love leetcode"]
        System.out.println(autocomplete.input(' ')); // ["i love you", "i love leetcode", "i a"]
        System.out.println(autocomplete.input('a')); // ["i a"]
        System.out.println(autocomplete.input('#')); // [] (increments "i a" frequency to 2)

        // Edge Case: New sentence with higher frequency
        System.out.println(autocomplete.input('i')); // Should show updated rankings

        // Edge Case: Special characters
        AutocompleteSystem ac2 = new AutocompleteSystem(new String[] { "abc" }, new int[] { 1 });
        System.out.println(ac2.input('a')); // ["abc"]
        System.out.println(ac2.input('b')); // ["abc"]
        System.out.println(ac2.input('c')); // ["abc"]
        System.out.println(ac2.input('#')); // [] (stores "abc")
    }

}
