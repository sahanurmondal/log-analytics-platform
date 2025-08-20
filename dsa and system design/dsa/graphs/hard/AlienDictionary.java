package graphs.hard;

import java.util.*;

/**
 * LeetCode 269: Alien Dictionary
 * https://leetcode.com/problems/alien-dictionary/
 *
 * Companies: Amazon, Microsoft, Google, Facebook, Airbnb
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description:
 * Given a list of words sorted lexicographically by the rules of an alien
 * language, return the order of the letters in the language.
 *
 * Constraints:
 * - 1 <= words.length <= 100
 * - 1 <= words[i].length <= 100
 * - words[i] consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it with topological sort?
 */
public class AlienDictionary {

    // Approach: Topological Sort - O(C) time, O(1) space where C is total number of
    // characters
    public String alienOrder(String[] words) {
        // Build the graph
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();

        // Initialize the graph with all characters
        for (String word : words) {
            for (char c : word.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                inDegree.putIfAbsent(c, 0);
            }
        }

        // Build edges by comparing adjacent words
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            // Check if word2 is a prefix of word1 (invalid case)
            int minLength = Math.min(word1.length(), word2.length());
            boolean found = false;

            for (int j = 0; j < minLength; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);

                if (c1 != c2) {
                    // Add edge from c1 to c2 if not already added
                    if (!graph.get(c1).contains(c2)) {
                        graph.get(c1).add(c2);
                        inDegree.put(c2, inDegree.get(c2) + 1);
                    }
                    found = true;
                    break; // Found first difference, no need to check further
                }
            }

            // Invalid case: ["abc", "ab"] - longer word comes before its prefix
            if (!found && word1.length() > word2.length()) {
                return "";
            }
        }

        // Perform topological sort using BFS
        StringBuilder result = new StringBuilder();
        Queue<Character> queue = new LinkedList<>();

        // Add characters with 0 in-degree to queue
        for (char c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                queue.add(c);
            }
        }

        while (!queue.isEmpty()) {
            char current = queue.poll();
            result.append(current);

            // Process neighbors
            for (char neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // If we couldn't visit all nodes, there must be a cycle
        return result.length() == inDegree.size() ? result.toString() : "";
    }

    public static void main(String[] args) {
        AlienDictionary solution = new AlienDictionary();
        // Edge Case 1: Normal case
        System.out.println(solution.alienOrder(new String[] { "wrt", "wrf", "er", "ett", "rftt" })); // "wertf"
        // Edge Case 2: Invalid order
        System.out.println(solution.alienOrder(new String[] { "z", "x", "z" })); // ""
        // Edge Case 3: Single word
        System.out.println(solution.alienOrder(new String[] { "abc" })); // "abc"
    }
}
