package miscellaneous.google;

import java.util.*;

/**
 * LeetCode 642: Design Search Autocomplete System
 * https://leetcode.com/problems/design-search-autocomplete-system/
 *
 * Description:
 * Design a search autocomplete system for a search engine. Users may input a
 * sentence (at least one word and end with a special character '#').
 * For each character they type except '#', return the top 3 historical hot
 * sentences that have the same prefix as the part of the sentence already
 * typed.
 * 
 * Company: Google
 * Difficulty: Hard
 * Asked: System design rounds in 2023-2024
 * 
 * Constraints:
 * - n == sentences.length
 * - n == times.length
 * - 1 <= n <= 100
 * - 1 <= sentences[i].length <= 100
 * - 1 <= times[i] <= 50
 * - c is a lowercase English letter, a hash '#', or space ' '
 * - Each tested sentence will be a sequence of characters c that end with the
 * character '#'
 */
public class AutocompleteSystem {

    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> sentences = new HashMap<>();
        boolean isEnd = false;
    }

    private TrieNode root;
    private TrieNode currNode;
    private StringBuilder currSentence;

    public AutocompleteSystem(String[] sentences, int[] times) {
        root = new TrieNode();
        currNode = root;
        currSentence = new StringBuilder();

        for (int i = 0; i < sentences.length; i++) {
            insert(sentences[i], times[i]);
        }
    }

    private void insert(String sentence, int times) {
        TrieNode node = root;
        for (char c : sentence.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
            node.sentences.put(sentence, node.sentences.getOrDefault(sentence, 0) + times);
        }
        node.isEnd = true;
    }

    public List<String> input(char c) {
        if (c == '#') {
            insert(currSentence.toString(), 1);
            currSentence = new StringBuilder();
            currNode = root;
            return new ArrayList<>();
        }

        currSentence.append(c);
        if (currNode != null) {
            currNode = currNode.children.get(c);
        }

        if (currNode == null) {
            return new ArrayList<>();
        }

        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>((a, b) -> {
            if (a.getValue() != b.getValue()) {
                return a.getValue() - b.getValue();
            }
            return b.getKey().compareTo(a.getKey());
        });

        for (Map.Entry<String, Integer> entry : currNode.sentences.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 3) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(0, pq.poll().getKey());
        }

        return result;
    }

    public static void main(String[] args) {
        String[] sentences = { "i love you", "island", "ironman", "i love leetcode" };
        int[] times = { 5, 3, 2, 2 };
        AutocompleteSystem system = new AutocompleteSystem(sentences, times);

        System.out.println(system.input('i')); // ["i love you", "island", "i love leetcode"]
        System.out.println(system.input(' ')); // ["i love you", "i love leetcode"]
        System.out.println(system.input('a')); // []
        System.out.println(system.input('#')); // []
    }
}
