package dp.linear.sequence;

import java.util.*;

/**
 * LeetCode 1048: Longest String Chain
 * https://leetcode.com/problems/longest-string-chain/
 *
 * Description:
 * You are given an array of words where each word consists of lowercase English
 * letters.
 * wordA is a predecessor of wordB if and only if we can insert exactly one
 * letter anywhere
 * in wordA without changing the order of the other characters to make it equal
 * to wordB.
 * Return the length of the longest possible word chain with words chosen from
 * the given list of words.
 *
 * Constraints:
 * - 1 <= words.length <= 1000
 * - 1 <= words[i].length <= 16
 * - words[i] only consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you find the actual chain?
 * - What if we need to handle very large inputs?
 *
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class LongestStringChain {

    // Approach 1: DP with HashMap - O(n^2 * m) time, O(n) space
    public int longestStrChain(String[] words) {
        Arrays.sort(words, (a, b) -> a.length() - b.length());
        Map<String, Integer> dp = new HashMap<>();
        int maxLength = 1;

        for (String word : words) {
            int currentMax = 1;

            // Try removing each character to find predecessors
            for (int i = 0; i < word.length(); i++) {
                String predecessor = word.substring(0, i) + word.substring(i + 1);
                if (dp.containsKey(predecessor)) {
                    currentMax = Math.max(currentMax, dp.get(predecessor) + 1);
                }
            }

            dp.put(word, currentMax);
            maxLength = Math.max(maxLength, currentMax);
        }

        return maxLength;
    }

    // Approach 2: DFS with Memoization - O(n^2 * m) time, O(n) space
    public int longestStrChainDFS(String[] words) {
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));
        Map<String, Integer> memo = new HashMap<>();
        int maxLength = 0;

        for (String word : words) {
            maxLength = Math.max(maxLength, dfs(word, wordSet, memo));
        }

        return maxLength;
    }

    private int dfs(String word, Set<String> wordSet, Map<String, Integer> memo) {
        if (memo.containsKey(word)) {
            return memo.get(word);
        }

        int maxLength = 1;

        // Try adding each character at each position
        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                String next = word.substring(0, i) + c + word.substring(i);
                if (wordSet.contains(next)) {
                    maxLength = Math.max(maxLength, 1 + dfs(next, wordSet, memo));
                }
            }
        }

        memo.put(word, maxLength);
        return maxLength;
    }

    // Approach 3: BFS - O(n^2 * m) time, O(n) space
    public int longestStrChainBFS(String[] words) {
        Map<String, Integer> wordToIndex = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            wordToIndex.put(words[i], i);
        }

        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            graph.add(new ArrayList<>());
        }

        // Build adjacency list
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            for (int j = 0; j <= word.length(); j++) {
                for (char c = 'a'; c <= 'z'; c++) {
                    String next = word.substring(0, j) + c + word.substring(j);
                    if (wordToIndex.containsKey(next)) {
                        graph.get(i).add(wordToIndex.get(next));
                    }
                }
            }
        }

        int maxLength = 1;
        for (int i = 0; i < words.length; i++) {
            maxLength = Math.max(maxLength, bfsFromNode(i, graph));
        }

        return maxLength;
    }

    private int bfsFromNode(int start, List<List<Integer>> graph) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(start);
        visited.add(start);

        int length = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            length++;

            for (int i = 0; i < size; i++) {
                int node = queue.poll();

                for (int neighbor : graph.get(node)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return length;
    }

    // Approach 4: Topological Sort - O(n^2 * m) time, O(n) space
    public int longestStrChainTopological(String[] words) {
        Map<String, Integer> wordToIndex = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            wordToIndex.put(words[i], i);
        }

        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[words.length];

        for (int i = 0; i < words.length; i++) {
            graph.add(new ArrayList<>());
        }

        // Build graph and calculate indegrees
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            for (int j = 0; j < word.length(); j++) {
                String predecessor = word.substring(0, j) + word.substring(j + 1);
                if (wordToIndex.containsKey(predecessor)) {
                    int predIndex = wordToIndex.get(predecessor);
                    graph.get(predIndex).add(i);
                    indegree[i]++;
                }
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        int[] dp = new int[words.length];

        for (int i = 0; i < words.length; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
                dp[i] = 1;
            }
        }

        int maxLength = 1;

        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (int neighbor : graph.get(node)) {
                dp[neighbor] = Math.max(dp[neighbor], dp[node] + 1);
                maxLength = Math.max(maxLength, dp[neighbor]);

                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return maxLength;
    }

    // Approach 5: Get Actual Chain - O(n^2 * m) time, O(n) space
    public List<String> getLongestChain(String[] words) {
        Arrays.sort(words, (a, b) -> a.length() - b.length());
        Map<String, Integer> dp = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        String longestWord = "";
        int maxLength = 0;

        for (String word : words) {
            int currentMax = 1;
            String currentParent = null;

            for (int i = 0; i < word.length(); i++) {
                String predecessor = word.substring(0, i) + word.substring(i + 1);
                if (dp.containsKey(predecessor) && dp.get(predecessor) + 1 > currentMax) {
                    currentMax = dp.get(predecessor) + 1;
                    currentParent = predecessor;
                }
            }

            dp.put(word, currentMax);
            if (currentParent != null) {
                parent.put(word, currentParent);
            }

            if (currentMax > maxLength) {
                maxLength = currentMax;
                longestWord = word;
            }
        }

        // Reconstruct the chain
        List<String> chain = new ArrayList<>();
        String current = longestWord;

        while (current != null) {
            chain.add(current);
            current = parent.get(current);
        }

        Collections.reverse(chain);
        return chain;
    }

    public static void main(String[] args) {
        LongestStringChain solution = new LongestStringChain();

        System.out.println("=== Longest String Chain Test Cases ===");

        // Test Case 1: Example from problem
        String[] words1 = { "a", "b", "ba", "bca", "bda", "bdca" };
        System.out.println("Test 1 - Words: " + Arrays.toString(words1));
        System.out.println("DP HashMap: " + solution.longestStrChain(words1));
        System.out.println("DFS: " + solution.longestStrChainDFS(words1));
        System.out.println("Topological: " + solution.longestStrChainTopological(words1));
        System.out.println("Actual Chain: " + solution.getLongestChain(words1));
        System.out.println("Expected: 4\n");

        // Test Case 2: Another example
        String[] words2 = { "xbc", "pcxbcf", "xb", "cxbc", "pcxbc" };
        System.out.println("Test 2 - Words: " + Arrays.toString(words2));
        System.out.println("DP HashMap: " + solution.longestStrChain(words2));
        System.out.println("Expected: 5\n");

        // Test Case 3: No chain possible
        String[] words3 = { "abde", "abc", "abd", "abcde", "ade", "ae", "1abde", "1abc" };
        System.out.println("Test 3 - Words: " + Arrays.toString(words3));
        System.out.println("DP HashMap: " + solution.longestStrChain(words3));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void performanceTest() {
        LongestStringChain solution = new LongestStringChain();

        // Generate test data
        List<String> wordList = new ArrayList<>();
        for (int len = 1; len <= 10; len++) {
            for (int i = 0; i < 50; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < len; j++) {
                    sb.append((char) ('a' + (j % 3)));
                }
                wordList.add(sb.toString());
            }
        }
        String[] testWords = wordList.toArray(new String[0]);

        System.out.println("=== Performance Test (Words: " + testWords.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.longestStrChain(testWords);
        long end = System.nanoTime();
        System.out.println("DP HashMap: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.longestStrChainDFS(testWords);
        end = System.nanoTime();
        System.out.println("DFS: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
