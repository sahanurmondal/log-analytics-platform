package design.hard;

import java.util.*;

/**
 * LeetCode 642: Design Search Autocomplete System
 * https://leetcode.com/problems/design-search-autocomplete-system/
 *
 * Description: Design a search autocomplete system for a search engine.
 * 
 * Constraints:
 * - n == sentences.length
 * - n == times.length
 * - 1 <= n <= 100
 * - 1 <= sentences[i].length <= 100
 * - 1 <= times[i] <= 50
 * - c is a lowercase English letter, a hash '#', or space ' '
 * - Each tested sentence will be a valid sentence
 * - Each tested sentence will not start or end with a space
 * - At most 5000 calls will be made to input
 *
 * Follow-up:
 * - Can you solve it efficiently with Trie?
 * 
 * Time Complexity: O(k * l) for input where k is matching sentences, l is
 * sentence length
 * Space Complexity: O(total characters in all sentences)
 * 
 * Company Tags: Google, Amazon
 */
public class AutocompleteSystem {

    class TrieNode {
        Map<Character, TrieNode> children;
        Map<String, Integer> sentences;

        TrieNode() {
            children = new HashMap<>();
            sentences = new HashMap<>();
        }
    }

    private TrieNode root;
    private TrieNode currentNode;
    private StringBuilder currentSentence;

    public AutocompleteSystem(String[] sentences, int[] times) {
        root = new TrieNode();
        currentNode = root;
        currentSentence = new StringBuilder();

        for (int i = 0; i < sentences.length; i++) {
            insert(sentences[i], times[i]);
        }
    }

    private void insert(String sentence, int count) {
        TrieNode node = root;

        for (char c : sentence.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
            node.sentences.put(sentence, node.sentences.getOrDefault(sentence, 0) + count);
        }
    }

    public List<String> input(char c) {
        if (c == '#') {
            // End of sentence, add to trie
            String sentence = currentSentence.toString();
            insert(sentence, 1);

            // Reset for next sentence
            currentSentence = new StringBuilder();
            currentNode = root;

            return new ArrayList<>();
        }

        currentSentence.append(c);

        if (currentNode == null || !currentNode.children.containsKey(c)) {
            currentNode = null;
            return new ArrayList<>();
        }

        currentNode = currentNode.children.get(c);

        // Get top 3 sentences
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>((a, b) -> {
            if (a.getValue() != b.getValue()) {
                return a.getValue() - b.getValue(); // Min heap by count
            }
            return b.getKey().compareTo(a.getKey()); // Max heap by lexicographic order
        });

        for (Map.Entry<String, Integer> entry : currentNode.sentences.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 3) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll().getKey());
        }

        Collections.reverse(result);
        return result;
    }

    public static void main(String[] args) {
        AutocompleteSystem system = new AutocompleteSystem(
                new String[] { "i love you", "island", "iroman", "i love leetcode" },
                new int[] { 5, 3, 2, 2 });

        System.out.println(system.input('i')); // Expected: ["i love you", "island", "i love leetcode"]
        System.out.println(system.input(' ')); // Expected: ["i love you", "i love leetcode"]
        System.out.println(system.input('a')); // Expected: []
        System.out.println(system.input('#')); // Expected: []
    }
}
