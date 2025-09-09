package grid.hard;

import java.util.*;

/**
 * LeetCode 269: Alien Dictionary
 * https://leetcode.com/problems/alien-dictionary/
 *
 * Description:
 * There is a new alien language that uses the English alphabet. However, the
 * order among the letters is unknown to you.
 * You are given a list of strings words from the alien language's dictionary,
 * where the strings in words are sorted
 * lexicographically by the rules of this new language.
 * Return a string of the unique letters in the new alien language sorted in
 * lexicographically increasing order by the new language's rules.
 * If there is no solution, return "". If there are multiple solutions, return
 * any of them.
 *
 * Constraints:
 * - 1 <= words.length <= 100
 * - 1 <= words[i].length <= 100
 * - words[i] consists of only lowercase English letters
 */
public class AlienDictionary {

    public String alienOrder(String[] words) {
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> indegree = new HashMap<>();

        // Initialize graph and indegree
        for (String word : words) {
            for (char c : word.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                indegree.putIfAbsent(c, 0);
            }
        }

        // Build graph
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            // Check if word1 is prefix of word2 but longer
            if (word1.length() > word2.length() && word1.startsWith(word2)) {
                return "";
            }

            // Find first different character
            for (int j = 0; j < Math.min(word1.length(), word2.length()); j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);

                if (c1 != c2) {
                    if (!graph.get(c1).contains(c2)) {
                        graph.get(c1).add(c2);
                        indegree.put(c2, indegree.get(c2) + 1);
                    }
                    break;
                }
            }
        }

        // Topological sort
        Queue<Character> queue = new LinkedList<>();
        for (char c : indegree.keySet()) {
            if (indegree.get(c) == 0) {
                queue.offer(c);
            }
        }

        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            char curr = queue.poll();
            result.append(curr);

            for (char next : graph.get(curr)) {
                indegree.put(next, indegree.get(next) - 1);
                if (indegree.get(next) == 0) {
                    queue.offer(next);
                }
            }
        }

        return result.length() == indegree.size() ? result.toString() : "";
    }

    public static void main(String[] args) {
        AlienDictionary solution = new AlienDictionary();

        String[] words1 = { "wrt", "wrf", "er", "ett", "rftt" };
        System.out.println(solution.alienOrder(words1)); // "wertf"

        String[] words2 = { "z", "x" };
        System.out.println(solution.alienOrder(words2)); // "zx"
    }
}
