package miscellaneous.google;

import java.util.*;

/**
 * Recent Google Interview Questions (2021-2024)
 * Compiled from LeetCode Discuss, GeeksforGeeks, and interview experiences
 */
public class RecentQuestions {

    /**
     * Question: Design a data structure that supports adding words and finding if a
     * string matches any previously added word.
     * The search can include '.' which matches any letter.
     * 
     * Company: Google
     * Difficulty: Medium
     * Asked: Multiple times in 2023-2024
     */
    class WordDictionary {
        class TrieNode {
            TrieNode[] children = new TrieNode[26];
            boolean isWord = false;
        }

        private TrieNode root;

        public WordDictionary() {
            root = new TrieNode();
        }

        public void addWord(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
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
                for (TrieNode child : node.children) {
                    if (child != null && searchHelper(word, index + 1, child)) {
                        return true;
                    }
                }
                return false;
            } else {
                int idx = c - 'a';
                return node.children[idx] != null && searchHelper(word, index + 1, node.children[idx]);
            }
        }
    }

    /**
     * Question: Given a string s and a dictionary of words, determine if s can be
     * segmented into a space-separated sequence of dictionary words.
     * Follow-up: Return all possible sentences.
     * 
     * Company: Google
     * Difficulty: Hard
     * Asked: Frequently in 2023-2024
     */
    public List<String> wordBreakII(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Map<String, List<String>> memo = new HashMap<>();
        return wordBreakHelper(s, wordSet, memo);
    }

    private List<String> wordBreakHelper(String s, Set<String> wordSet, Map<String, List<String>> memo) {
        if (memo.containsKey(s)) {
            return memo.get(s);
        }

        List<String> result = new ArrayList<>();
        if (s.isEmpty()) {
            result.add("");
            return result;
        }

        for (int i = 1; i <= s.length(); i++) {
            String prefix = s.substring(0, i);
            if (wordSet.contains(prefix)) {
                List<String> suffixes = wordBreakHelper(s.substring(i), wordSet, memo);
                for (String suffix : suffixes) {
                    result.add(prefix + (suffix.isEmpty() ? "" : " " + suffix));
                }
            }
        }

        memo.put(s, result);
        return result;
    }

    /**
     * Question: Design a system to store and retrieve time-based key-value pairs.
     * 
     * Company: Google
     * Difficulty: Medium
     * Asked: Multiple times in 2022-2023
     */
    class TimeMap {
        private Map<String, TreeMap<Integer, String>> map;

        public TimeMap() {
            map = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            map.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
        }

        public String get(String key, int timestamp) {
            if (!map.containsKey(key))
                return "";

            TreeMap<Integer, String> timeMap = map.get(key);
            Integer floorKey = timeMap.floorKey(timestamp);
            return floorKey == null ? "" : timeMap.get(floorKey);
        }
    }

    public static void main(String[] args) {
        RecentQuestions solution = new RecentQuestions();

        // Test WordDictionary
        WordDictionary wd = solution.new WordDictionary();
        wd.addWord("bad");
        wd.addWord("dad");
        wd.addWord("mad");
        System.out.println(wd.search("pad")); // false
        System.out.println(wd.search("bad")); // true
        System.out.println(wd.search(".ad")); // true

        // Test Word Break II
        List<String> wordDict = Arrays.asList("cat", "cats", "and", "sand", "dog");
        System.out.println(solution.wordBreakII("catsanddog", wordDict));

        // Test TimeMap
        TimeMap tm = solution.new TimeMap();
        tm.set("foo", "bar", 1);
        System.out.println(tm.get("foo", 1)); // "bar"
        System.out.println(tm.get("foo", 3)); // "bar"
    }
}
